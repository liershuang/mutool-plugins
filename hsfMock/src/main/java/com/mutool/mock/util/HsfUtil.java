package com.mutool.mock.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.mutool.mock.model.HsfServiceInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述：hsf工具类<br>
 * 作者：les<br>
 * 日期：2021/2/3 18:56<br>
 */
public class HsfUtil {

    /**
     * 解析jar包hsf接口配置信息
     *
     * @param jarPath
     * @return
     */
    public static List<HsfServiceInfo> getHsfModelList(String jarPath) {
        List<String> xmlFilePathList = JarUtil.listFileFromJar(jarPath, (name) -> name.endsWith(".xml"));
        if (CollUtil.isEmpty(xmlFilePathList)) {
            return Collections.EMPTY_LIST;
        }

        List<HsfServiceInfo> hsfModelList = new ArrayList<>();
        xmlFilePathList.forEach(i -> {
            String xmlContent = JarUtil.readJarFile(jarPath, i);
            Document hsfxml = cn.hutool.core.util.XmlUtil.readXML(xmlContent);
            List<Element> eles = XmlUtil.getElements(hsfxml.getDocumentElement(), "hsf:provider");
            for (Element ele : eles) {
                HsfServiceInfo hsfModel = new HsfServiceInfo();
                hsfModel.setId(ele.getAttribute("id"));
                hsfModel.setInterfaceName(ele.getAttribute("interface"));
                hsfModel.setRef(ele.getAttribute("ref"));
                hsfModel.setVersion(ele.getAttribute("version"));
                String clientTimeoutStr = ele.getAttribute("clientTimeout");
                hsfModel.setClientTimeout(StrUtil.isBlank(clientTimeoutStr) ? null : Integer.parseInt(clientTimeoutStr));

                hsfModelList.add(hsfModel);
            }
        });
        return hsfModelList;
    }


}
