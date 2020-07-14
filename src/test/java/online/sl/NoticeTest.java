package online.sl;

import lombok.extern.slf4j.Slf4j;
import online.sl.service.NoticeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest(classes = Bootstrap.class,webEnvironment = SpringBootTest.WebEnvironment.NONE )
@RunWith(SpringRunner.class)
public class NoticeTest {

    private NoticeService noticeService;

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Test
    public void noticeTest() throws IOException {
        boolean b = Timestamp.valueOf("2020-06-12 00:00:00").toLocalDateTime().toLocalDate()
                .compareTo(Timestamp.valueOf("2020-06-12 00:00:00").toLocalDateTime().toLocalDate())== 0;

        Assert.assertTrue(b);
        noticeService.sendSameMessage(1, "2020-01-01 11:21", "2020-01-01 11:21");

    }

}
