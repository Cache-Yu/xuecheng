package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;
import sun.misc.IOUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;


    public QueryResponseResult findList( int page,  int size, QueryPageRequest queryPageRequest) {
        if(queryPageRequest==null){
            queryPageRequest=new QueryPageRequest();
        }
        CmsPage cmsPage=new CmsPage();
        //设置站点ID
        if(!StringUtils.isEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置模板ID
        if(!StringUtils.isEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //设置页面别名
        if(!StringUtils.isEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        ExampleMatcher exampleM=ExampleMatcher.matching()
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());


        Example<CmsPage> example=Example.of(cmsPage,exampleM);

        if(page<=0){
            page=1; //当前页码
        }
        page=page-1;

        if (size<=0){
            size=10; //每页展示数据的多少
        }

        Pageable pageable= PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);

        QueryResult queryResult=new QueryResult();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult queryResponseResultj=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResultj;
    }

    public CmsPageResult add(CmsPage cmsPage) {
        //根据pageName，pageWebPath，siteId查询cmsPage对象,
        //先判断数据库里面是否已经存在将要添加的页面
        CmsPage page = cmsPageRepository.findByPageNameAndPageWebPathAndSiteId(cmsPage.getPageName(), cmsPage.getPageWebPath(), cmsPage.getSiteId());
       if(page!=null){
           //校验页面是否存在，已存在则抛出异常
           ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
       }
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
   }

    public CmsPage findById(String id){
        Optional<CmsPage> page = cmsPageRepository.findById(id);
        if (page.isPresent()){
            CmsPage cmsPage = page.get();
            return cmsPage;
        }
        return null;
    }



    public CmsPageResult update(String id,CmsPage cmsPage){
          CmsPage one = this.findById(id);
        if(one!=null){
            one.setTemplateId(cmsPage.getTemplateId());
           //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
                //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

            one.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = cmsPageRepository.save(one);
            if(save!=null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //删除页面
    public ResponseResult del(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){//先判断要删除的页面是否已经存在
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //页面静态化
    public String getPageHtml(String pageId){
        //获得 页面模型数据
        Map model = this.getModelByPageId(pageId);
      if(model==null){
          //获取的页面数据模型为空
          ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
      }
        //获得页面模板
        String template = this.getTemplateByPageId(pageId);
           if(StringUtils.isEmpty(template)){
               //页面模板为空
               ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
           }
        //执行页面的静态化
        String html = this.generateHtml(template, model);
           if(StringUtils.isEmpty(html)){
               ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
           }

        return html;
    }
    //执行静态化的操作
    private String generateHtml(String templateContent,Map map) {
        Configuration configuration=new Configuration(Configuration.getVersion());

        StringTemplateLoader templateLoader=new StringTemplateLoader();
        templateLoader.putTemplate("template",templateContent);

        configuration.setTemplateLoader(templateLoader);

        Template template = null;
        String s=null;
        try {
            template = configuration.getTemplate("template", "utf-8");
           s = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
          return s;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
      return null;
    }

    @Autowired
    GridFsTemplate gridFsTemplate;
    //得到页面模板

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    private   String getTemplateByPageId(String pageId){
        CmsPage page = this.findById(pageId);
        if (page==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = page.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional optional = cmsTemplateRepository.findById(templateId);
        CmsTemplate template=null;
        if (optional.isPresent()){
       template = (CmsTemplate)optional.get();
        }

        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(template.getTemplateFileId())));
        //打开下载流对象
        GridFSDownloadStream downloadStream=gridFSBucket.openDownloadStream(file.getObjectId());
        GridFsResource gridFsResource=new GridFsResource(file,downloadStream);

        String templateContext = null;
        try {
            templateContext = org.apache.commons.io.IOUtils.toString(gridFsResource.getInputStream(), "utf-8");

            return templateContext;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    @Autowired
    RestTemplate restTemplate;
     //得到数据模型
    private Map   getModelByPageId(String pageId){
        CmsPage page = this.findById(pageId);
        if (page==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String dataUrl = page.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();

        return body;

    }
 }





