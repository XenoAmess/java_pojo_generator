package generated.demo3;

@lombok.Generated
@lombok.Data
@org.springframework.data.mongodb.core.mapping.Document("table_name2")
public class TableName2 {

    @org.springframework.data.mongodb.core.mapping.Field("a_a")
    @com.fasterxml.jackson.annotation.JsonProperty("a_a")
    private java.lang.String aA;

    @org.springframework.data.mongodb.core.mapping.Field("b_b")
    @com.fasterxml.jackson.annotation.JsonProperty("b_b")
    private java.lang.Integer bB;

    @org.springframework.data.mongodb.core.mapping.Field("c_c")
    @com.fasterxml.jackson.annotation.JsonProperty("c_c")
    private java.io.Serializable cC;

    @org.springframework.data.mongodb.core.mapping.Field("d_d")
    @com.fasterxml.jackson.annotation.JsonProperty("d_d")
    private java.lang.Long dD;

    @org.springframework.data.mongodb.core.mapping.Field("i")
    @com.fasterxml.jackson.annotation.JsonProperty("i")
    private org.bson.types.ObjectId i;

    @org.springframework.data.mongodb.core.mapping.Field("e_e")
    @com.fasterxml.jackson.annotation.JsonProperty("e_e")
    private generated.demo3.EE eE;

    @org.springframework.data.mongodb.core.mapping.Field("_id")
    @com.fasterxml.jackson.annotation.JsonProperty("_id")
    @org.springframework.data.annotation.Id
    private java.lang.String id;

    @org.springframework.data.mongodb.core.mapping.Field("f_f")
    @com.fasterxml.jackson.annotation.JsonProperty("f_f")
    private java.util.List<TableName23> fF;

    @org.springframework.data.mongodb.core.mapping.Field("g_g")
    @com.fasterxml.jackson.annotation.JsonProperty("g_g")
    private java.lang.Void gG;
}
