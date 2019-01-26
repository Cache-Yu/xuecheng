package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.contrller.StudentController;
import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Test
    public void test2() throws IOException, TemplateException {
        Configuration configuration=new Configuration(Configuration.getVersion());

       //模板内容，这里测试时使用简单的字符串作为模板
         String templateString=""+"<html>\n"+" <head></head>\n"+" <body>\n"+"名称：${name}\n"+"</body>\n"+"</html>";
        StringTemplateLoader templateLoader =new StringTemplateLoader();
        templateLoader.putTemplate("template",templateString);
        configuration.setTemplateLoader(templateLoader);
        Template template = configuration.getTemplate("template", "utf-8");

        Map map=new HashMap();
        map.put("name","黑马程序员");
        String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        System.out.println(s);
        InputStream inputStream = IOUtils.toInputStream(s);
        FileOutputStream outputStream = new FileOutputStream(new File("d:test1.html"));
        int copy = IOUtils.copy(inputStream, outputStream);

    }

    @Test
    public void test1() throws IOException, TemplateException {
        //获取配置类
        Configuration configuration=new Configuration(Configuration.getVersion());
        //获取模板的路径
        String path = StudentController.class.getResource("/").getPath();

        //E:\xcEduService\test‐freemarke
        configuration.setDirectoryForTemplateLoading(new File("E:\\xcEduService\\test‐freemarke\\src\\test\\resources\\templates"));
        //设置字符编码集
        configuration.setDefaultEncoding("utf-8");
        //获得模板
        Template template = configuration.getTemplate("test1.ftl");
        //获得数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        System.out.println("=================="+content);

        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream = new FileOutputStream(new File("d:test1.html"));
        int copy = IOUtils.copy(inputStream, outputStream);
    }


    Map getMap(){
        Map map =new HashMap();


        map.put("name","成都传智播客！");

        //向数据模型放数据

        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());

        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);

        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus",stus);
        //准备map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        //向数据模型放数据
        map.put("stu1",stu1);
        //向数据模型放数据
        map.put("point",312857234835702l);
        map.put("stuMap",stuMap);

        return  map;
    }

    @Autowired
    RestTemplate restTemplate;
    @Test
    public void test4(){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map map = forEntity.getBody();

    }

    @Test
    public void  test5(){

    }
}
