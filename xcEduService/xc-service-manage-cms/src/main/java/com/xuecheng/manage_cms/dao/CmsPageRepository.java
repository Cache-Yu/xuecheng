package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
   //根据页面名称来查询
    CmsPage findByPageName(String name);
    //根据页面名称和类型查询
    CmsPage findByPageNameAndPageType(String pageName,String pageType);
    //根据pageName，pageWebPath，siteId查询cmsPage对象
    CmsPage findByPageNameAndPageWebPathAndSiteId(String pageName,String pageWebPath,String siteId);
}
