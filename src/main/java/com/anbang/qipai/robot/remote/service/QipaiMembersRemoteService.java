package com.anbang.qipai.robot.remote.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.anbang.qipai.robot.remote.vo.CommonRemoteVO;

/**
 * 会员中心远程服务
 * 
 * @author lsc
 *
 */
@FeignClient("qipai-members")
public interface QipaiMembersRemoteService {

	@RequestMapping(value = "/gold/members_withdraw")
	public CommonRemoteVO gold_members_withdraw(@RequestBody String[] memberIds,
			@RequestParam(value = "amount") int amount, @RequestParam(value = "textSummary") String textSummary);

	@RequestMapping(value = "/gold/givegoldtomembers")
	public CommonRemoteVO gold_givegoldtomembers(@RequestBody String[] memberIds,
			@RequestParam(value = "amount") int amount, @RequestParam(value = "textSummary") String textSummary);

}
