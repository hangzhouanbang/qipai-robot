package com.anbang.qipai.robot.remote.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.anbang.qipai.robot.remote.vo.CommonRemoteVO;

@FeignClient("qipai-xiuxianchang")
public interface QipaiXiuxianchangRemoteService {

	@RequestMapping(value = "/gold/members_withdraw")
	public CommonRemoteVO gold_members_withdraw(@RequestBody String[] memberIds,
			@RequestParam(value = "amount") int amount, @RequestParam(value = "textSummary") String textSummary);

	@RequestMapping(value = "/gold/members_givegold")
	public CommonRemoteVO gold_givegoldtomembers(@RequestBody String[] memberIds,
			@RequestParam(value = "amount") int amount, @RequestParam(value = "textSummary") String textSummary);

}
