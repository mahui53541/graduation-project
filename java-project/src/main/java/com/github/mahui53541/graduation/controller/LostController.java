package com.github.mahui53541.graduation.controller;

import com.github.mahui53541.graduation.service.LostService;
import com.github.mahui53541.graduation.vo.LostUserVO;
import com.github.mahui53541.graduation.vo.LostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * java类简单作用描述
 *
 * @Description: java类作用描述
 * @Author: MaHui
 * @CreateDate: 2018/5/4 13:13
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "/lost")
public class LostController {
    @Autowired
    private LostService lostService;
    /**
     * 查询失物招领信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/{pageNum}/{pageSize}")
    public Object findByPage(@PathVariable("pageNum") int pageNum,
                             @PathVariable("pageSize") int pageSize,
                             @RequestParam(name = "keyword",required = false) String keyword,
                             @RequestParam(name = "startDate",required = false)Date startDate,
                             @RequestParam(name = "endDate",required = false)Date endDate){
        return lostService.queryByPage(pageNum,pageSize,keyword,startDate,endDate);
    }

    /**
     * 查询寻物启事详情信息
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public LostUserVO getLostDetail(@PathVariable("id") int id){
        return lostService.getLostDetail(id);
    }

    /**
     * 发布
     * @return
     * @RequestParam(name = "file",required = false) MultipartFile file,
     */
    @PostMapping(value = "")
    @PreAuthorize("hasRole('USER')")
    public Object postLost(@RequestBody LostVO lostVO){
        return ResponseEntity.ok(lostService.postLost(lostVO));
    }
}
