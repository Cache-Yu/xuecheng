package com.xuecheng.manage_cms.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        for (CmsPage page : all) {
            System.out.println(page);
        }
    }


    @Test
    public void testPageFindAll(){
        Pageable pageable= PageRequest.of(1,10);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        for (CmsPage page : all) {
            System.out.println(page);
        }
    }

    @Test
    public void testPageFindAllByExample(){
        Pageable pageable= PageRequest.of(0,10);

        //定义查询值对象
        CmsPage cmsPage =new CmsPage();
       // cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
     //   cmsPage.setTemplateId("5ad9a24d68db5239b8fef199");q
        cmsPage.setPageAliase("轮播");
      //定义匹配器
       // ExampleMatcher exampleMatcher=ExampleMatcher.matching();
       // exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());


        ExampleMatcher exampleMatcher=ExampleMatcher.matching()
               .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        for (CmsPage page : all) {
            System.out.println(page);
        }
        System.out.println("总共的记录条数："+all.getTotalElements());
    }
    @Test
    public void testInsert(){
        CmsPage cmsPage=new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());

        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    @Test
    public void testdelete(){
       cmsPageRepository.deleteById("5c3f357ed3dab750cc25f230");
    }


    @Test
    public void testUpdate() {
        Optional<CmsPage> byId = cmsPageRepository.findById("5c3f3619d3dab741e47e7875");
        if (byId.isPresent()) {
            CmsPage cmsPage = byId.get();
            cmsPage.setPageName("难忘是四月,那是邂逅你的季节");

            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }

    }
        @Test
        public void testConditionByPageName(){
            CmsPage byPageName = cmsPageRepository.findByPageName("难忘是四月,那是邂逅你的季节");
            System.out.println(byPageName);
        }

    @Test
    public void testConditionByPageNameAndType(){
        CmsPage pageType = cmsPageRepository.findByPageNameAndPageType("难忘是四月,那是邂逅你的季节", "1");
        System.out.println(pageType);
    }
    @Autowired
    RestTemplate restTemplate;
@Test
    public void testRestTemplate(){
    ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
    Map body = forEntity.getBody();
    System.out.println(body);
}

@Autowired
    GridFsTemplate gridFsTemplate;
@Test
    public void  test6() throws FileNotFoundException {
    File file =new File("E:\\xcEduService\\test‐freemarke\\src\\main\\resources\\templates\\index_banner.ftl");
    FileInputStream inputStream =new FileInputStream(file);
    ObjectId store = gridFsTemplate.store(inputStream, "轮播图测试模板");
    String ID = store.toString();
    System.out.println(ID);
}

    @Autowired
    GridFSBucket gridFSBucket;
@Test
public  void test7() throws IOException {
    String id="5c49a26dad86513608713bb6";
    GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));

    //打开下载流
    GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
    //得到GridFsResource对象
    GridFsResource gridFsResource=new GridFsResource(gridFSFile,gridFSDownloadStream);
  //  System.out.println(gridFsResource.getFile());
    IOUtils.copy(gridFsResource.getInputStream(),new FileOutputStream("G:\\bb.jpg"));
}

@Test
    public void test8(){

    String id="5c49a26dad86513608713bb6";
    gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));

 }

 @Autowired
    PageService pageService;
 @Test
    public void testPreView(){
     String pageHtml = pageService.getPageHtml("5c446c0cad8651035088b036");
     System.out.println(pageHtml);
 }

 public static final int count=10;
 CountDownLatch countDownLatch=new CountDownLatch(count);

 CountDownLatch countDownLatch1=new CountDownLatch(count);
    @Test
    public void testDXC(){
        System.out.println("执行开始！");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
         DxcThread thread=new DxcThread();
         new Thread(thread).start();
         countDownLatch.countDown();
     }
     try {
         countDownLatch1.await();
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
        long endTime = System.currentTimeMillis();
        System.out.println("一共消耗的时间"+(endTime-startTime));
        System.out.println("执行结束！");
 }
 class DxcThread implements Runnable{
    String url="http://www.baidu.com";
    Integer num=0;
     @Override
     public void run() {
         try {
             countDownLatch.await();
             ResponseEntity forEntity = restTemplate.getForEntity(url, String.class);
         //    System.out.println(forEntity.getBody());
             num++;
           System.out.println("已经被访问的次数："+(num));
             countDownLatch1.countDown();
         } catch (InterruptedException e) {
             e.printStackTrace();
         }

     }
 }
}
