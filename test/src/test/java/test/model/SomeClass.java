package test.model;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

public class SomeClass {

  @Deprecated
  public void notThatInteresting() {

  }

  @ApiOperation(value = "Creates something")
  @ApiResponses(value = {@ApiResponse(code = 204, message = "")})
  public void other() {
    System.out.println("checking it");
  }
}
