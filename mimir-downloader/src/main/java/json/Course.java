package json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Course {
  private String name;
  @SerializedName("allCoursework")
  private List<AssignmentMetadata> assignments;

  public List<AssignmentMetadata> getAssignments() {
    return assignments;
  }

  public void setAssignments(List<AssignmentMetadata> assignments) {
    this.assignments = assignments;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
