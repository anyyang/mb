package cn.liuyangjob.controller;

import cn.liuyangjob.JoinService.JoinerService;
import cn.liuyangjob.bean.PlatformActivitiesJoin;
import cn.liuyangjob.service.JoinService;
import cn.liuyangjob.util.HttpUtil;
import cn.liuyangjob.util.MyThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by  liuyang
 * 2019/8/22    17:09
 * cn.liuyangjob
 * All Right Reserved by liuyang.
 **/
@RestController
public class MyJoinPlayerController {
    @Autowired
    JoinService joinService;
    @Autowired
    JoinerService joinerService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/index")
    public Object index() {
        stringRedisTemplate.opsForValue().set("abc","123");
        String result =  stringRedisTemplate.opsForValue().get("abc");
        return "welecome"+result;
    }

    /**
     * 查询单个用户
     *
     * @param id
     * @return
     */
    @RequestMapping("/user/{id}")
    public Object getUserInfo(@PathVariable Long id) {
        Optional<PlatformActivitiesJoin> result = joinService.findById(id);
        return result.get();
    }

    /**
     * 查询活动所有用户
     *
     * @param id
     * @return
     */
    @RequestMapping("/active/{id}")
    public Object getPlatforActiveties(@PathVariable Long id) {
        List<PlatformActivitiesJoin> joins = joinService.getPlatformActivitiesJoinByPlatformActivitiesId(id);
        return joins;
    }

    /**
     * 查询活动指定区间的用户
     *
     * @return
     */
    @RequestMapping("/activeuser")
    public Object getQujianUser(HttpServletRequest request, String aid, String start, String end) {
        System.out.println("");
        List<PlatformActivitiesJoin> joins = joinService.findAll(new Specification<PlatformActivitiesJoin>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<PlatformActivitiesJoin> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                list.add(criteriaBuilder.equal(root.get("state").as(Integer.class), 0));
                list.add(criteriaBuilder.equal(root.get("platformActivitiesId").as(Integer.class), 4));
                /**
                 * 查询区间
                 */
                list.add(criteriaBuilder.between(root.get("index"), 1, 10));
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return joins;
    }



    @RequestMapping("/resigerUserbatch")
    public Object resigerNewUser(int start, int end, int aid) {

        List<PlatformActivitiesJoin> lists = joinerService.getSpecial(start, end, aid);
        StringBuffer res = new StringBuffer();
        int baseTime = 0;
        lists.forEach(join -> {
            int sleepTime = baseTime + new Random().nextInt(60000) + new Random().nextInt(60000)+ new Random().nextInt(60000)+new Random().nextInt(60000)+new Random().nextInt(60000)+new Random().nextInt(60000);
            System.out.println(Thread.currentThread().getName() + "*****sleep:" + sleepTime / 1000 + "s");
            Runnable saverun = () -> {
                try {
                    Thread.sleep(sleepTime);
                    String result = joinerService.fillAndSendObject(join);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            MyThreadFactory.getExecutorService().execute(saverun);
        });

        return res.toString();
    }

}
