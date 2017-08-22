import com.maceo.investment.datacrawler.Utils;
import org.joda.time.DateTime;
import org.junit.Test;

public class TestUtils {
    @Test
    public void testSimple() {
        DateTime krNowYYYYMMDD = Utils.krNowYYYYMMDD();
    }
}
