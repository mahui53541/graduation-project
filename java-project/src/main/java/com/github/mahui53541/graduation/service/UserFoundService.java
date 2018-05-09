package com.github.mahui53541.graduation.service;

import com.github.mahui53541.graduation.mapper.UserFoundMapper;
import com.github.mahui53541.graduation.model.UserFound;
import com.github.mahui53541.graduation.model.UserLost;
import com.github.mahui53541.graduation.vo.MessageVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * java类简单作用描述
 *
 * @Description: java类作用描述
 * @Author: MaHui
 * @CreateDate: 2018/5/4 13:09
 * @Version: 1.0
 */
@Service("userFoundService")
public class UserFoundService extends BaseService<UserFoundMapper,UserFound> {
    public List<MessageVO> getMessage(Boolean isRead, Integer userId){
        return userFoundMapper.getMessage(isRead,userId);
    }

    /**
     * 标记为已读
     * @param messageId
     * @param type
     * @return
     */
    public int markMessage(int messageId,int type){
        if(type==1){
            UserFound userFound=userFoundMapper.selectByPrimaryKey(messageId);
            userFound.setIsRead(true);
            return userFoundMapper.updateByPrimaryKey(userFound);
        }else if(type==2){
            UserLost userLost=userLostMapper.selectByPrimaryKey(messageId);
            userLost.setIsRead(true);
            return userLostMapper.updateByPrimaryKey(userLost);
        }
        return -1;
    }
}
