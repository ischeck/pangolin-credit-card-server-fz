package cn.fintecher.pangolin.data.creation.web;

import cn.fintecher.pangolin.data.creation.respository.BaseCaseRepository;
import cn.fintecher.pangolin.data.creation.respository.PersonalRepository;
import cn.fintecher.pangolin.entity.domain.Personal;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by ChenChang on 2017/8/4.
 */
@RestController
@RequestMapping("/api/person")
@Api(value = "个人信息", description = "个人信息")
public class PersonController {
    private final Logger log = LoggerFactory.getLogger(PersonController.class);
    @Autowired
    private PersonalRepository personalRepository;
    @Autowired
    private BaseCaseRepository baseCaseRepository;


    @PostMapping(value = "/testPost")
    public ResponseEntity<Void> testPost(HttpServletRequest request) throws UnsupportedEncodingException {
        String str = request.getParameter("issuerIdView");

        byte[] isoByte = str.getBytes("ISO-8859-1");
        System.err.println(new String(isoByte, "GBK"));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/addPersonList")
    public ResponseEntity<Void> addPersonList() {
        //起个名字，在最后面统计信息中会打印出来
       /* for (int j = 0; j < 2000; j++) {

            StopWatch stopWatch = new StopWatch("执行百万条数据插入");

            //记录本地方法的耗时
            stopWatch.start("执行百万条数据插入 数据准备");
            List<Personal> personList = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < 10000; i++) {
                Map<String, String> map = RandomValue.getAddress();
                Personal personal = new Personal();
//                personal.setBirthday(DateRandom.randomDate());
                personal.setPersonalName(map.get("name"));
                personal.setPersonalName(CreateIDCardNo.getRandomID());
                personal.setSex(Sex.MALE);
//                personal.setIdCardAddress(map.get("road"));
                personal.setPostCode(map.get("road"));
                personal.setMarital(Marital.MARRIED);
                personal.setId(personal.getCardNo());
              //  personal.setPersonalContactSet(new HashSet<>());
                int c = rand.nextInt(10) + 1;
                while (c > 0) {
                    Map<String, String> cmap = RandomValue.getAddress();
                    PersonalContact personalContact = new PersonalContact();
                   // personalContact.setRelation(Relationship.CHILD);
                    personalContact.setName(map.get("name"));
//                    personalContact.setPhone(map.get("tel"));
                    personalContact.setIdCard(CreateIDCardNo.getRandomID());
//                    personal.getPersonalContacts().add(personalContact);
                    c--;
                }
                personList.add(personal);
            }
            stopWatch.stop();
            stopWatch.start("执行百万条数据插入 数据插入");
            personalRepository.saveAll(personList);
            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());
        }*/
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updatePersonList")
    public ResponseEntity<Void> updatePersonList() {
        StopWatch stopWatch = new StopWatch("执行数据更新");

        stopWatch.start("执行数据更新 数据准备");
        List<Personal> personList = Lists.newArrayList(personalRepository.findAll());
        for (Personal personal : personList) {
//            personal.setEducation("大学");
        }
        stopWatch.stop();
        stopWatch.start("执行数据更新 数据插入");
        personalRepository.saveAll(personList);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete() {
        personalRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
