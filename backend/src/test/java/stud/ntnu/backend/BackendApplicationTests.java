package stud.ntnu.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import stud.ntnu.backend.startup.PoiStartupLoader;

@SpringBootTest
class BackendApplicationTests {
  @MockitoBean
  private PoiStartupLoader poiStartupLoader;

  @Test
  void contextLoads() {
  }

}
