<!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <title>Hello World!</title>
     </head>

      <body>
      Hello  ${name}!
      <HR>
      <TABLE>
          <tr>
              <th>编号</th>
              <th>姓名</th>
              <th>年龄</th>
              <th>钱包</th>
              <th>生日</th>

          <#--    <th>朋友列表</th>-->
           <#--   <th>最好的朋友</th>-->
          </tr>
          <#list stus as stu>
          <tr>
              <td> ${stu_index+1}</td>
              <td  <#if stu.name=='小红'>style="background:#2cff23"</#if> > ${stu.name}</td>
              <td> ${stu.age}</td>
              <td  <#if stu.money gt 200 >style="background:burlywood"</#if> > ${stu.money}</td>
              <td>${stu.birthday?datetime}</td>
          </tr>
          </#list>
      </TABLE>
      学生个数：${stus?size}
      <h3>遍历MAP中的数据</h3>
      姓名：${stuMap.stu1.name}<br>
      年龄：${stuMap.stu1.age}<br>

      <#list  stuMap?keys as k>
            姓名：${stuMap[k].name}<br>
            年龄：${stuMap[k].age}<br>
      </#list>
     价格： ${point?c}
      </body>
</html>