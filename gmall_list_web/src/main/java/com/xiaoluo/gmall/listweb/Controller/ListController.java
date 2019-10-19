package com.xiaoluo.gmall.listweb.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xiaoluo.gmall.bean.BaseAttrInfo;
import com.xiaoluo.gmall.bean.BaseAttrValue;
import com.xiaoluo.gmall.bean.SkuLsParams;
import com.xiaoluo.gmall.bean.SkuLsResult;
import com.xiaoluo.gmall.service.ListService;
import com.xiaoluo.gmall.service.ManagerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@CrossOrigin
public class ListController {

	@Reference
	private ListService listService;

	@Reference
	private ManagerService managerService;

	@GetMapping("list.html")
	public String getList(SkuLsParams skuLsParams, Model model){
		SkuLsResult skuLsResult = listService.searchSkuLsInfo(skuLsParams);
		ArrayList<BaseAttrValue> breadcrumbList = new ArrayList<>();


		//获取平台属性
		List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
		List<BaseAttrInfo> baseAttrInfoList = managerService.ListBaseAttr(attrValueIdList);
		if(baseAttrInfoList != null && baseAttrInfoList.size() >0){
			Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator();
			while (iterator.hasNext()){
				BaseAttrInfo baseAttrInfo = iterator.next();
				List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
				for (BaseAttrValue baseAttrValue : attrValueList) {
					String[] valueIds = skuLsParams.getValueId();
					if(valueIds != null && valueIds.length > 0){
						for (int i = 0; i < valueIds.length; i++) {
							String valueId = valueIds[i];
							if(valueId.equals(baseAttrValue.getId())){
								iterator.remove();
								baseAttrValue.setValueName(baseAttrInfo.getAttrName() + ":" +baseAttrValue.getValueName());
								breadcrumbList.add(baseAttrValue);
								baseAttrValue.setUrlParam(makeUrlParams(skuLsParams,valueId));

							}
						}
					}

				}
			}
		}


		String urlParams = makeUrlParams(skuLsParams);
		model.addAttribute("skuLsResult",skuLsResult);
		model.addAttribute("baseAttrInfoList",baseAttrInfoList);
		model.addAttribute("urlParams",urlParams);
		model.addAttribute("breadcrumbList",breadcrumbList);
		model.addAttribute("keyword",skuLsParams.getKeyword());
		model.addAttribute("totalPages",skuLsResult.getTotalPages());
		model.addAttribute("pageNo",skuLsParams.getPageNo());

		return "list";

	}

	private String makeUrlParams(SkuLsParams skuLsParams,String... excludeValueIds) {
		//keyword
		StringBuffer stringBuffer = new StringBuffer();
		String keyword = skuLsParams.getKeyword();
		if(!StringUtils.isEmpty(keyword)){
			stringBuffer.append("?keyword=");
			stringBuffer.append(keyword);
		}else{
			String catalog3Id = skuLsParams.getCatalog3Id();
			if(!StringUtils.isEmpty(catalog3Id)){
				stringBuffer.append("?catalog3Id=");
				stringBuffer.append(catalog3Id);
			}
		}
		String[] valueIds = skuLsParams.getValueId();
		if(valueIds != null && valueIds.length > 0){
			for (int i = 0; i < valueIds.length; i++) {

				String valueId = valueIds[i];
				if (excludeValueIds!=null && excludeValueIds.length>0){
					String excludeValueId = excludeValueIds[0];
					if (excludeValueId.equals(valueId)){
						// 跳出代码，后面的参数则不会继续追加【后续代码不会执行】
						// 不能写break；如果写了break；其他条件则无法拼接！
						continue;
					}
				}
				stringBuffer.append("&valueId=");
				stringBuffer.append(valueId);

			}
		}
		return stringBuffer.toString();
	}
}
