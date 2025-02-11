package agora.beet.exit;

import agora.beet.model.DeclsVariable;
import agora.beet.model.DeclsClass;
import agora.beet.model.DeclsExit;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static agora.beet.main.GenerateDeclsFile.*;
import static agora.beet.main.GenerateDeclsFile.packageName;
import static agora.beet.model.DeclsClass.setDeclsClassEnterAndExit;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class NestedArraysObjectExitTest {

    @Test
    public void testGenerationOfNestedArraysObjectExit() {

        deleteAllDeclsClasses();

        String oasPath = "src/test/resources/sampleAPI/swagger_nestedArraysObject.yaml";

        // Equivalent to the getOpenAPISpecification private function
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolveFully(true);
        parseOptions.setFlatten(true);
        OpenAPI specification = new OpenAPIV3Parser().read(oasPath, null, parseOptions);

        Paths paths = specification.getPaths();

        for(Map.Entry<String, PathItem> path: paths.entrySet()) {
            PathItem pathItem = path.getValue();

            for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry: pathItem.readOperationsMap().entrySet()) {

                Operation operation = operationEntry.getValue();
                String operationEndpoint = path.getKey().replace("/", "");

                // Set the operation name for the .decls file
                String operationName = getOperationName(operation, operationEntry, operationEndpoint);

                // Extracting the input parameters
                String objectName = operationName + HIERARCHY_SEPARATOR + "Input";

                setDeclsClassEnterAndExit(packageName, operationEndpoint, operationName,
                        objectName, operation);

                List<DeclsClass> allDeclsClasses = getAllDeclsClasses();
                assertEquals("Incorrect number of classes", allDeclsClasses.size(), 1);

                DeclsClass declsClassEnterAndExit = allDeclsClasses.get(0);

                System.out.println(declsClassEnterAndExit);

                // CLASS
                assertEquals("Incorrect package name", packageName, declsClassEnterAndExit.getPackageName());
                assertEquals("Incorrect class name", "sampleEndpoint", declsClassEnterAndExit.getClassName());
                assertEquals("The size of the list of enters is not 4", 4, declsClassEnterAndExit.getDeclsEnters().size());
                assertEquals("The size of the list of exits is not 4", 4, declsClassEnterAndExit.getDeclsExits().size());

                // OBJECT (ONLY EXIT)
                List<DeclsExit> declsExits = declsClassEnterAndExit.getDeclsExits();

                // FATHER
                DeclsExit fatherDeclsExit = declsExits.get(0);

                String exitName = packageName + "." + operationEndpoint + "." + operationName + HIERARCHY_SEPARATOR + "200" + "(" + packageName + "." + operationName + HIERARCHY_SEPARATOR + "Input" + ")";
                assertEquals("Incorrect exit name", exitName, fatherDeclsExit.getExitName());


                // VARIABLES
                // ENTER
                // Only the father
                DeclsVariable enterDeclsFatherVariable1 = fatherDeclsExit.getEnterDeclsVariables();
                assertEquals("Incorrect variable name", "input", enterDeclsFatherVariable1.getVariableName());
                assertEquals("Incorrect var-kind", "variable", enterDeclsFatherVariable1.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Input", enterDeclsFatherVariable1.getDecType());
                assertEquals("Incorrect repType", "hashcode", enterDeclsFatherVariable1.getRepType());
                assertNull("The enclosing var should be null", enterDeclsFatherVariable1.getEnclosingVar());
                assertFalse("This variable should not be an array", enterDeclsFatherVariable1.isArray());
                assertEquals("Unexpected number of son variables", 2, enterDeclsFatherVariable1.getEnclosedVariables().size());

                // EXIT
                DeclsVariable exitDeclsFatherVariable1 = fatherDeclsExit.getExitDeclsVariables();

                // Father
                assertEquals("Incorrect variable name", "return", exitDeclsFatherVariable1.getVariableName());
                assertEquals("Incorrect var-kind", "return", exitDeclsFatherVariable1.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Output" + HIERARCHY_SEPARATOR + "200", exitDeclsFatherVariable1.getDecType());
                assertEquals("Incorrect repType", "hashcode", exitDeclsFatherVariable1.getRepType());
                assertNull("The enclosing var should be null", exitDeclsFatherVariable1.getEnclosingVar());
                assertFalse("This variable should not be an array", exitDeclsFatherVariable1.isArray());
                assertEquals("Unexpected number of son variables", 2, exitDeclsFatherVariable1.getEnclosedVariables().size());

                // Sons (array)
                List<DeclsVariable> declsSonVariables1 = exitDeclsFatherVariable1.getEnclosedVariables();

                DeclsVariable location1 = declsSonVariables1.get(0);
                assertEquals("Incorrect variable name", "return.locations", location1.getVariableName());
                assertEquals("Incorrect var-kind", "field locations", location1.getVarKind());
                assertEquals("Incorrect decType", packageName + ".locations[]", location1.getDecType());
                assertEquals("Incorrect repType", "hashcode", location1.getRepType());
                assertEquals("Incorrect enclosing var", "return", location1.getEnclosingVar());
                assertFalse("This variable should not be an array", location1.isArray());
                assertEquals("Unexpected number of son variables", 0, location1.getEnclosedVariables().size());

                DeclsVariable location2 = declsSonVariables1.get(1);
                assertEquals("Incorrect variable name", "return.locations[..]", location2.getVariableName());
                assertEquals("Incorrect var-kind", "array", location2.getVarKind());
                assertEquals("Incorrect decType", packageName + ".locations[]", location2.getDecType());
                assertEquals("Incorrect repType", "hashcode[]", location2.getRepType());
                assertEquals("Incorrect enclosing var", "return.locations", location2.getEnclosingVar());
                assertTrue("This variable should be an array", location2.isArray());
                assertEquals("Unexpected number of son variables", 0, location2.getEnclosedVariables().size());

                String repeatedArray = ".array";

                exitName = packageName + "." + operationEndpoint + "." + operationName + HIERARCHY_SEPARATOR + "200" + HIERARCHY_SEPARATOR + "locations" +
                        repeatedArray + "(" + packageName + "." + operationName + HIERARCHY_SEPARATOR + "Input" + ")";

                DeclsExit declsExit = declsExits.get(1);
                assertEquals("Incorrect exit name", exitName, declsExit.getExitName());

                // VARIABLES
                // ENTER
                // Only the father
                DeclsVariable enterDeclsFatherVariable = declsExit.getEnterDeclsVariables();
                assertEquals("Incorrect variable name", "input", enterDeclsFatherVariable.getVariableName());
                assertEquals("Incorrect var-kind", "variable", enterDeclsFatherVariable.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Input", enterDeclsFatherVariable.getDecType());
                assertEquals("Incorrect repType", "hashcode", enterDeclsFatherVariable.getRepType());
                assertNull("The enclosing var should be null", enterDeclsFatherVariable.getEnclosingVar());
                assertFalse("This variable should not be an array", enterDeclsFatherVariable.isArray());
                assertEquals("Unexpected number of son variables", 2, enterDeclsFatherVariable.getEnclosedVariables().size());

                // EXIT
                DeclsVariable exitDeclsFatherVariable = declsExit.getExitDeclsVariables();

                // Father
                assertEquals("Incorrect variable name", "return", exitDeclsFatherVariable.getVariableName());
                assertEquals("Incorrect var-kind", "return", exitDeclsFatherVariable.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Output" + HIERARCHY_SEPARATOR + "200" + HIERARCHY_SEPARATOR + "locations" + repeatedArray, exitDeclsFatherVariable.getDecType());
                assertEquals("Incorrect repType", "hashcode", exitDeclsFatherVariable.getRepType());
                assertNull("The enclosing var should be null", exitDeclsFatherVariable.getEnclosingVar());
                assertFalse("This variable should not be an array", exitDeclsFatherVariable.isArray());
                assertEquals("Unexpected number of son variables", 2, exitDeclsFatherVariable.getEnclosedVariables().size());

                // Sons (array)
                List<DeclsVariable> declsSonVariables = exitDeclsFatherVariable.getEnclosedVariables();

                DeclsVariable array1 = declsSonVariables.get(0);
                assertEquals("Incorrect variable name", "return.array", array1.getVariableName());
                assertEquals("Incorrect var-kind", "field array", array1.getVarKind());
                assertEquals("Incorrect decType", packageName + ".array[]", array1.getDecType());
                assertEquals("Incorrect repType", "hashcode", array1.getRepType());
                assertEquals("Incorrect enclosing var", "return", array1.getEnclosingVar());
                assertFalse("This variable should not be an array", array1.isArray());
                assertEquals("Unexpected number of son variables", 0, array1.getEnclosedVariables().size());

                DeclsVariable array2 = declsSonVariables.get(1);
                assertEquals("Incorrect variable name", "return.array[..]", array2.getVariableName());
                assertEquals("Incorrect var-kind", "array", array2.getVarKind());
                assertEquals("Incorrect decType", packageName + ".array[]", array2.getDecType());
                assertEquals("Incorrect repType", "hashcode[]", array2.getRepType());
                assertEquals("Incorrect enclosing var", "return.array", array2.getEnclosingVar());
                assertTrue("This variable should be an array", array2.isArray());
                assertEquals("Unexpected number of son variables", 0, array2.getEnclosedVariables().size());

                repeatedArray = ".array.array";

                exitName = packageName + "." + operationEndpoint + "." + operationName + HIERARCHY_SEPARATOR + "200" + HIERARCHY_SEPARATOR + "locations" +
                        repeatedArray + "(" + packageName + "." + operationName + HIERARCHY_SEPARATOR + "Input" + ")";

                declsExit = declsExits.get(3);
                assertEquals("Incorrect exit name", exitName, declsExit.getExitName());

                // VARIABLES
                // ENTER
                // Only the father
                enterDeclsFatherVariable = declsExit.getEnterDeclsVariables();
                assertEquals("Incorrect variable name", "input", enterDeclsFatherVariable.getVariableName());
                assertEquals("Incorrect var-kind", "variable", enterDeclsFatherVariable.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Input", enterDeclsFatherVariable.getDecType());
                assertEquals("Incorrect repType", "hashcode", enterDeclsFatherVariable.getRepType());
                assertNull("The enclosing var should be null", enterDeclsFatherVariable.getEnclosingVar());
                assertFalse("This variable should not be an array", enterDeclsFatherVariable.isArray());
                assertEquals("Unexpected number of son variables", 2, enterDeclsFatherVariable.getEnclosedVariables().size());

                // EXIT
                exitDeclsFatherVariable = declsExit.getExitDeclsVariables();

                // Father
                assertEquals("Incorrect variable name", "return", exitDeclsFatherVariable.getVariableName());
                assertEquals("Incorrect var-kind", "return", exitDeclsFatherVariable.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Output" + HIERARCHY_SEPARATOR + "200" + HIERARCHY_SEPARATOR + "locations" + repeatedArray, exitDeclsFatherVariable.getDecType());
                assertEquals("Incorrect repType", "hashcode", exitDeclsFatherVariable.getRepType());
                assertNull("The enclosing var should be null", exitDeclsFatherVariable.getEnclosingVar());
                assertFalse("This variable should not be an array", exitDeclsFatherVariable.isArray());
                assertEquals("Unexpected number of son variables", 2, exitDeclsFatherVariable.getEnclosedVariables().size());

                // Sons (array)
                declsSonVariables = exitDeclsFatherVariable.getEnclosedVariables();

                array1 = declsSonVariables.get(0);
                assertEquals("Incorrect variable name", "return.array", array1.getVariableName());
                assertEquals("Incorrect var-kind", "field array", array1.getVarKind());
                assertEquals("Incorrect decType", packageName + ".array[]", array1.getDecType());
                assertEquals("Incorrect repType", "hashcode", array1.getRepType());
                assertEquals("Incorrect enclosing var", "return", array1.getEnclosingVar());
                assertFalse("This variable should not be an array", array1.isArray());
                assertEquals("Unexpected number of son variables", 0, array1.getEnclosedVariables().size());

                array2 = declsSonVariables.get(1);
                assertEquals("Incorrect variable name", "return.array[..]", array2.getVariableName());
                assertEquals("Incorrect var-kind", "array", array2.getVarKind());
                assertEquals("Incorrect decType", packageName + ".array[]", array2.getDecType());
                assertEquals("Incorrect repType", "hashcode[]", array2.getRepType());
                assertEquals("Incorrect enclosing var", "return.array", array2.getEnclosingVar());
                assertTrue("This variable should be an array", array2.isArray());
                assertEquals("Unexpected number of son variables", 0, array2.getEnclosedVariables().size());


                DeclsExit finalDeclsExit = declsExits.get(2);
                exitName = packageName + "." + operationEndpoint + "." + operationName + HIERARCHY_SEPARATOR + "200" + HIERARCHY_SEPARATOR + "locations" +
                        "(" + packageName + "." + operationName + HIERARCHY_SEPARATOR + "Input" + ")";
                assertEquals("Incorrect exit name", exitName, finalDeclsExit.getExitName());

                // VARIABLES
                // ENTER
                // Only the father
                DeclsVariable enterDeclsFatherVariable2 = finalDeclsExit.getEnterDeclsVariables();
                assertEquals("Incorrect variable name", "input", enterDeclsFatherVariable2.getVariableName());
                assertEquals("Incorrect var-kind", "variable", enterDeclsFatherVariable2.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Input", enterDeclsFatherVariable2.getDecType());
                assertEquals("Incorrect repType", "hashcode", enterDeclsFatherVariable2.getRepType());
                assertNull("The enclosing var should be null", enterDeclsFatherVariable2.getEnclosingVar());
                assertFalse("This variable should not be an array", enterDeclsFatherVariable2.isArray());
                assertEquals("Unexpected number of son variables", 2, enterDeclsFatherVariable2.getEnclosedVariables().size());

                // EXIT
                DeclsVariable exitDeclsFatherVariable2 = finalDeclsExit.getExitDeclsVariables();

                // Father
                assertEquals("Incorrect variable name", "return", exitDeclsFatherVariable2.getVariableName());
                assertEquals("Incorrect var-kind", "return", exitDeclsFatherVariable2.getVarKind());
                assertEquals("Incorrect decType", packageName + ".sampleEndpointId" + HIERARCHY_SEPARATOR + "Output" + HIERARCHY_SEPARATOR + "200" + HIERARCHY_SEPARATOR + "locations", exitDeclsFatherVariable2.getDecType());
                assertEquals("Incorrect repType", "hashcode", exitDeclsFatherVariable2.getRepType());
                assertNull("The enclosing var should be null", exitDeclsFatherVariable2.getEnclosingVar());
                assertFalse("This variable should not be an array", exitDeclsFatherVariable2.isArray());
                assertEquals("Unexpected number of son variables", 2, exitDeclsFatherVariable2.getEnclosedVariables().size());

                // Sons (Only the first one)
                declsSonVariables = exitDeclsFatherVariable2.getEnclosedVariables();
                DeclsVariable locationId = declsSonVariables.get(0);
                assertEquals("Incorrect variable name", "return.locationId", locationId.getVariableName());
                assertEquals("Incorrect var-kind", "field locationId", locationId.getVarKind());
                assertEquals("Incorrect decType", "java.lang.String", locationId.getDecType());
                assertEquals("Incorrect repType", "java.lang.String", locationId.getRepType());
                assertEquals("Incorrect enclosing var", "return", locationId.getEnclosingVar());
                assertFalse("This variable should not be an array", locationId.isArray());
                assertEquals("Unexpected number of son variables", 0, locationId.getEnclosedVariables().size());

            }

        }


    }

}
