import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.List;

public class DailyTest {

  @Test
  @SneakyThrows
  public void test4All() {
    sout(null);
  }

  private void sout(Object o) {
    System.out.println((JSON.toJSON(o)));
  }


}
