/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.restroutes.internal;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import java.util.List;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class RouterGenerator {
    private final JDefinedClass routerClass;
    private final RouteClassGenerator generator;
    private final int generateMods;

    public RouterGenerator(JDefinedClass routerClass, RouteClassGenerator generator, int generateMods) {
        this.routerClass = routerClass;
        this.generator = generator;
        this.generateMods = generateMods;
    }

    public RouterGenerator(JDefinedClass routerClass, RouteClassGenerator generator) {
        this(routerClass, generator, JMod.PUBLIC);
    }

    public JDefinedClass build(List<RouteClass> routeClassList) {
        for (RouteClass routeClass : routeClassList) {
            JDefinedClass definedClass = generator.generate(routeClass);
            if (definedClass == null) continue;

            addRouterMethod(routerClass, definedClass);
        }

        return routerClass;
    }

    private void addRouterMethod(JDefinedClass router, JDefinedClass definedClass) {
        final String className = definedClass.fullName();
        final String firstLetter = Character.toString(definedClass.name().charAt(0));
        final String methodName = definedClass.name().split("Resource$")[0].replaceFirst("^"+firstLetter, firstLetter.toLowerCase());
        final JMethod method = router.method(generateMods, definedClass, methodName);
        final JBlock block = method.body();
        block.directStatement("return restAdapter.create(" + className + ".class);");
    }
}
