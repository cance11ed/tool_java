import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.junit.Test;

public class DailyTest {

  @Test
  @SneakyThrows
  public void test4All() {
    log(null);
  }

  private void log(Object o) {
    System.out.println((JSON.toJSON(o)));
  }
}
