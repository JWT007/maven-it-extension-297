package me.jwt007.maven.plugins.ite.issue297.its;

import java.io.File;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@DisplayName("HelloMojo:")
@MavenJupiterExtension
@MavenRepository
public class HelloMojo3IT {

  @Nested
  @DisplayName("Nested:")
  class NestedClass {

    @DisplayName("... test version")
    @MavenGoal("compile")
    @MavenTest
    @SystemProperty(value = "versioning.disable", content="true") // disable maven-git-versioning-extension for IT tests
    public void test_001(MavenExecutionResult result) {

      assertThat(result)
        .isSuccessful()
        .satisfies(r -> {
          assertThat(new File(r.getMavenProjectResult().getTargetProjectDirectory(), ".git-versioned-pom.xml"))
            .as("Versioned POM File")
            .doesNotExist();
        });

    }

  }

}
