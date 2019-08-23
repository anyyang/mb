package cn.liuyangjob.JoinService;

import cn.liuyangjob.PhoneData.PhoneData;
import cn.liuyangjob.bean.PlatformActivitiesJoin;
import cn.liuyangjob.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by  liuyang
 * 2019/8/23    14:03
 * cn.liuyangjob.JoinerService
 * All Right Reserved by liuyang.
 **/

@Service("joinerService")
public class JoinerService {
    @Autowired
    cn.liuyangjob.service.JoinService joinService;

    public String fillAndSendObject(PlatformActivitiesJoin join) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", PhoneData.getMobile());
        params.put("nickname", join.getNickname());
        params.put("code", "999888");
        params.put("describe", "1");
        params.put("platformActivitiesId", "5");
        params.put("provinceId", join.getProvinceId() == null ? "" : join.getProvinceId() + "");
        params.put("cityId", join.getCityId() == null ? "" : join.getCityId() + "");
        params.put("townId", join.getTownId() == null ? "" : join.getTownId() + "");
        params.put("address", join.getAddress());
        params.put("imgsrcs", join.getImgsrcs());
        String res = null;
        try {
            String url = "http://www.gjyunying.com/";
           // String url = "http://www.t.gjyydh.com/";
            res = HttpUtil.postForm(url + "active/mb/signUpDo", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 数据库的条件查询
     *
     * @param start
     * @param end
     * @param aid
     * @return
     */
    public List<PlatformActivitiesJoin> getSpecial(int start, int end, long aid) {
        List<PlatformActivitiesJoin> joins = joinService.findAll(new Specification<PlatformActivitiesJoin>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<PlatformActivitiesJoin> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(criteriaBuilder.equal(root.get("state").as(Integer.class), 0));
                list.add(criteriaBuilder.equal(root.get("platformActivitiesId").as(Integer.class), 3));
                list.add(criteriaBuilder.equal(root.get("state").as(Integer.class), 0));//只查询审核通过的
                /**
                 * 查询区间
                 */
                list.add(criteriaBuilder.between(root.get("index"), start, end));
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return joins;
    }
}