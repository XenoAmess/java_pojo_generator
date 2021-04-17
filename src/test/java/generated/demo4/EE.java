package generated.demo4;

import lombok.Generated;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.String;
import java.lang.Integer;
import java.lang.Long;

@Generated
@Data
public class EE {

    @Field("a_a")
    @JsonProperty("a_a")
    private String aA;

    @Field("b_b")
    @JsonProperty("b_b")
    private Integer bB;

    @Field("c_c")
    @JsonProperty("c_c")
    private Integer cC;

    @Field("d_d")
    @JsonProperty("d_d")
    private Long dD;
}
