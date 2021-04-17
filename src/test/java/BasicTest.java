
import com.xenoamess.java_pojo_generator.GuessClassGuessGenerator;
import com.xenoamess.java_pojo_generator.JavaCodeBakeProperties;
import com.xenoamess.java_pojo_generator.JavaFilesBaker;
import com.xenoamess.java_pojo_generator.guess.GuessClassGuess;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

@org.springframework.data.mongodb.core.mapping.Document
public class BasicTest {

    @org.springframework.data.mongodb.core.mapping.Field("a_a")
    @org.springframework.data.annotation.Id
    @com.fasterxml.jackson.annotation.JsonProperty("a_a")
    @com.alibaba.fastjson.annotation.JSONField(name = "a_a")
    private Integer a;

    @Test
    public void basicTest() {

        JavaCodeBakeProperties properties = new JavaCodeBakeProperties();

        properties.setOutputFolder("src/test/java");

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("a_a", "a_a");
        hashMap.put("i", new ObjectId());
        hashMap.put("_id", "idId");
        hashMap.put("b_b", 1);
        hashMap.put("c_c", 'c');
        hashMap.put("d_d", 2L);
        hashMap.put("g_g", null);
        Map<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("a_a", "a_a");
        hashMap2.put("b_b", 1);
        hashMap2.put("c_c", 2);
        hashMap2.put("d_d", 2L);
        hashMap.put("e_e", hashMap2);
        hashMap.put("f_f", Arrays.asList(hashMap2, hashMap2));
        GuessClassGuess generatedClass = new GuessClassGuessGenerator().generate(
                "table_name",
                Collections.singletonList(hashMap)
        );

        new JavaFilesBaker()
                .bake(
                        generatedClass,
                        properties
                );

        properties.setIfBeautify(false);
        properties.setPackageName("generated.demo2");

        new JavaFilesBaker()
                .bake(
                        generatedClass,
                        properties
                );

        properties.setIfBeautify(true);
        properties.setPackageName("generated.demo3");

        GuessClassGuess generatedClass2 = new GuessClassGuessGenerator().generate(
                "table_name2",
                Arrays.asList(hashMap, hashMap2)
        );

        new JavaFilesBaker()
                .bake(
                        generatedClass2,
                        properties
                );

        properties.setIfUsingImports(true);
        properties.setPackageName("generated.demo4");

        new JavaFilesBaker()
                .bake(
                        generatedClass,
                        properties
                );

    }
}
