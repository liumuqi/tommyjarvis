package json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode implements Comparable<TreeNode> {
    @JsonProperty("value")
    private String value;
    @JsonProperty("label")
    private String desc;
    @JsonSerialize(using = JacksonSerializers.AdcodeSerializer.class)
    private Integer jobType;
    @JsonManagedReference
    @JsonProperty("children")
    private List<TreeNode> contents;//= new ArrayList<>();


    public TreeNode(String value, String desc) {
        this.value = value;
        this.desc = desc;
        this.jobType = null;
    }

    public TreeNode(String value, String desc, Integer jobType) {
        this.value = value;
        this.desc = desc;
        this.jobType = jobType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public List<TreeNode> getContents() {
        return contents;
    }

    public void setContents(List<TreeNode> contents) {
        this.contents = contents;
    }

    @Override
    public int compareTo(TreeNode o) {
        return this.getValue().compareTo(o.getValue());
    }
}

