package generated.demo2;

@lombok.Generated
@lombok.Data
@org.springframework.data.mongodb.core.mapping.Document("table_name")
public class table_name {

    @org.springframework.data.mongodb.core.mapping.Field("a_a")
    @com.fasterxml.jackson.annotation.JsonProperty("a_a")
    private java.lang.String a_a;

    @org.springframework.data.mongodb.core.mapping.Field("b_b")
    @com.fasterxml.jackson.annotation.JsonProperty("b_b")
    private java.lang.Integer b_b;

    @org.springframework.data.mongodb.core.mapping.Field("c_c")
    @com.fasterxml.jackson.annotation.JsonProperty("c_c")
    private java.lang.Character c_c;

    @org.springframework.data.mongodb.core.mapping.Field("d_d")
    @com.fasterxml.jackson.annotation.JsonProperty("d_d")
    private java.lang.Long d_d;

    @org.springframework.data.mongodb.core.mapping.Field("i")
    @com.fasterxml.jackson.annotation.JsonProperty("i")
    private org.bson.types.ObjectId i;

    @org.springframework.data.mongodb.core.mapping.Field("e_e")
    @com.fasterxml.jackson.annotation.JsonProperty("e_e")
    private generated.demo2.table_name2 e_e;

    @org.springframework.data.mongodb.core.mapping.Field("_id")
    @com.fasterxml.jackson.annotation.JsonProperty("_id")
    @org.springframework.data.annotation.Id
    private java.lang.String _id;

    @org.springframework.data.mongodb.core.mapping.Field("f_f")
    @com.fasterxml.jackson.annotation.JsonProperty("f_f")
    private java.util.List<table_name3> f_f;

    @org.springframework.data.mongodb.core.mapping.Field("g_g")
    @com.fasterxml.jackson.annotation.JsonProperty("g_g")
    private java.lang.Void g_g;
}
