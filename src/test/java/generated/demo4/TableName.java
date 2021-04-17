package generated.demo4;

import lombok.Generated;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.String;
import java.lang.Integer;
import java.lang.Character;
import java.lang.Long;
import org.bson.types.ObjectId;
import generated.demo4.EE;
import org.springframework.data.annotation.Id;
import java.util.List;
import java.lang.Void;

@Generated
@Data
@Document("table_name")
public class TableName {

    @Field("a_a")
    @JsonProperty("a_a")
    private String aA;

    @Field("b_b")
    @JsonProperty("b_b")
    private Integer bB;

    @Field("c_c")
    @JsonProperty("c_c")
    private Character cC;

    @Field("d_d")
    @JsonProperty("d_d")
    private Long dD;

    @Field("i")
    @JsonProperty("i")
    private ObjectId i;

    @Field("e_e")
    @JsonProperty("e_e")
    private EE eE;

    @Field("_id")
    @JsonProperty("_id")
    @Id
    private String id;

    @Field("f_f")
    @JsonProperty("f_f")
    private List<TableName3> fF;

    @Field("g_g")
    @JsonProperty("g_g")
    private Void gG;
}
