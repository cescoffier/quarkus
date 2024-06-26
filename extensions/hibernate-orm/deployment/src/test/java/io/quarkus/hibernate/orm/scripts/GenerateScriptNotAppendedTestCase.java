package io.quarkus.hibernate.orm.scripts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.hibernate.orm.MyEntity;
import io.quarkus.hibernate.orm.TestTags;
import io.quarkus.test.QuarkusDevModeTest;

@Tag(TestTags.DEVMODE)
public class GenerateScriptNotAppendedTestCase {

    @RegisterExtension
    static QuarkusDevModeTest runner = new QuarkusDevModeTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource("application-generate-script.properties", "application.properties")
                    .addClasses(MyEntity.class));

    @RepeatedTest(2)
    public void verifyScriptIsOverwritten() throws Exception {
        String script = Files.readString(Path.of(GenerateScriptNotAppendedTestCase.class.getResource("/create.sql").toURI()));
        assertEquals(1, Pattern.compile("create table MyEntity").matcher(script).results().count());
    }

}
