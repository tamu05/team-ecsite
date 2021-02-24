package jp.co.internous.crocus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.crocus.model.domain.MstDestination;
import jp.co.internous.crocus.model.mapper.MstDestinationMapper;
import jp.co.internous.crocus.model.mapper.TblCartMapper;
import jp.co.internous.crocus.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.crocus.model.session.LoginSession;

@Controller
@RequestMapping("/crocus/settlement")
public class SettlementController {
	
	@Autowired
	private LoginSession loginSession;
	
	@Autowired
	private MstDestinationMapper destinationMapper;
	
	private Gson gson = new Gson();
	
	@Autowired
	private TblCartMapper cartMapper;
	
	@Autowired
	private TblPurchaseHistoryMapper purchaseHistoryMapper;
	
	@RequestMapping("/")
	public String index(Model m) {
		
		int userId = loginSession.getUserId();
		
		List<MstDestination> destinations = destinationMapper.findByUserId(userId);
		m.addAttribute("destinations", destinations);
		m.addAttribute("loginSession",loginSession);
		
		return "settlement";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {
		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		String id = map.get("destinationId");
		
		int userId = loginSession.getUserId();
		
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("destinationId", id);
		parameter.put("userId", userId);
		int insertCount = purchaseHistoryMapper.insert(parameter);
		
		int deleteCount = 0;
		
		if(insertCount > 0){
			deleteCount = cartMapper.deleteByUserId(userId);
		}
		
		return deleteCount == insertCount;
	}
}