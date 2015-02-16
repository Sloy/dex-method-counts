/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.persistent.dex;

import com.android.dexdeps.ClassRef;
import com.android.dexdeps.DexData;
import com.android.dexdeps.MethodRef;
import com.android.dexdeps.Output;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DexMethodCounts {
    private final PrintStream log = System.out;

    enum Filter {
        ALL,
        DEFINED_ONLY,
        REFERENCED_ONLY
    }

    public MethodCountNode generate(
            DexData dexData, boolean includeClasses, String packageFilter,
            int maxDepth, Filter filter) {
        MethodRef[] methodRefs = getMethodRefs(dexData, filter);
        MethodCountNode packageTree = new MethodCountNode();

        for (MethodRef methodRef : methodRefs) {
            String classDescriptor = methodRef.getDeclClassName();
            String packageName = includeClasses ?
                Output.descriptorToDot(classDescriptor).replace('$', '.') :
                Output.packageNameOnly(classDescriptor);
            if (packageFilter != null &&
                    !packageName.startsWith(packageFilter)) {
                continue;
            }
            String packageNamePieces[] = packageName.split("\\.");
            MethodCountNode packageNode = packageTree;
            for (int i = 0; i < packageNamePieces.length && i < maxDepth; i++) {
                packageNode.count++;
                String name = packageNamePieces[i];
                if (packageNode.children.containsKey(name)) {
                    packageNode = packageNode.children.get(name);
                } else {
                    MethodCountNode childPackageNode = new MethodCountNode();
                    packageNode.children.put(name, childPackageNode);
                    packageNode = childPackageNode;
                }
            }
            packageNode.count++;
        }
        return packageTree;
    }

    private MethodRef[] getMethodRefs(DexData dexData, Filter filter) {
        MethodRef[] methodRefs = dexData.getMethodRefs();
        log.println("Read in " + methodRefs.length + " method IDs.");
        if (filter == Filter.ALL) {
            return methodRefs;
        }

        ClassRef[] externalClassRefs = dexData.getExternalReferences();
        log.println("Read in " + externalClassRefs.length +
                " external class references.");
        Set<MethodRef> externalMethodRefs = new HashSet<MethodRef>();
        for (ClassRef classRef : externalClassRefs) {
            for (MethodRef methodRef : classRef.getMethodArray()) {
                externalMethodRefs.add(methodRef);
            }
        }
        log.println("Read in " + externalMethodRefs.size() +
                " external method references.");
        List<MethodRef> filteredMethodRefs = new ArrayList<MethodRef>();
        for (MethodRef methodRef : methodRefs) {
            boolean isExternal = externalMethodRefs.contains(methodRef);
            if ((filter == Filter.DEFINED_ONLY && !isExternal) ||
                (filter == Filter.REFERENCED_ONLY && isExternal)) {
                filteredMethodRefs.add(methodRef);
            }
        }
        log.println("Filtered to " + filteredMethodRefs.size() + " " +
                (filter == Filter.DEFINED_ONLY ? "defined" : "referenced") +
                " method IDs.");
        return filteredMethodRefs.toArray(
            new MethodRef[filteredMethodRefs.size()]);
    }
}
