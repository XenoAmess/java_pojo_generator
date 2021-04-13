import com.xenoamess.java_pojo_generator.GuessClassGuessGenerator;
import com.xenoamess.java_pojo_generator.JavaCodeBakeProperties;
import com.xenoamess.java_pojo_generator.JavaFilesBaker;
import com.xenoamess.java_pojo_generator.guess.GuessClassGuess;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;

@org.springframework.data.mongodb.core.mapping.Document
public class BasicTest {

    @org.springframework.data.mongodb.core.mapping.Field("a_a")
    @Id
    private Integer a;

    @Test
    public void basicTest() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("a_a", "a_a");
        hashMap.put("b_b", 1);
        hashMap.put("c_c", 'c');
        hashMap.put("d_d", 2L);
        Map<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("a_a", "a_a");
        hashMap2.put("b_b", 1);
        hashMap2.put("c_c", 2);
        hashMap2.put("d_d", 2L);
        hashMap.put("e_e", hashMap2);
        hashMap.put("f_f", Arrays.asList(hashMap2, hashMap2));
        GuessClassGuess generatedClass = new GuessClassGuessGenerator().generate(
                "kohar",
                Arrays.asList(hashMap)
        );

        JavaCodeBakeProperties properties = new JavaCodeBakeProperties();
        new JavaFilesBaker()
                .bake(
                        generatedClass,
                        properties
                );

        properties.setIfBeautify(false);
        properties.setPackageName("demo2");

        new JavaFilesBaker()
                .bake(
                        generatedClass,
                        properties
                );

        properties.setIfBeautify(true);
        properties.setPackageName("demo3");

        GuessClassGuess generatedClass2 = new GuessClassGuessGenerator().generate(
                "kohar2",
                Arrays.asList(hashMap, hashMap2)
        );

        new JavaFilesBaker()
                .bake(
                        generatedClass2,
                        properties
                );

    }
}
