import com.xenoamess.java_pojo_generator.GuessClassGuessGenerator;
import com.xenoamess.java_pojo_generator.guess.GuessClassGuess;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class BasicTest {

    @Test
    public void basicTest() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("a", "a");
        hashMap.put("b", 1);
        hashMap.put("c", 'c');
        hashMap.put("d", 2L);
        Map<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("a", "a");
        hashMap2.put("b", 1);
        hashMap2.put("c", 'c');
        hashMap2.put("d", 2L);
        hashMap.put("e", hashMap2);
        hashMap.put("f", Arrays.asList(hashMap2, hashMap2));
        GuessClassGuess generatedClass = new GuessClassGuessGenerator().generate(
                "kohar",
                Arrays.asList(hashMap)
        );
    }
}
