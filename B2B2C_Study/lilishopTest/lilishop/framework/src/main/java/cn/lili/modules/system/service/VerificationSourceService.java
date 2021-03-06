package cn.lili.modules.system.service;

import cn.lili.cache.CachePrefix;
import cn.lili.modules.system.entity.dos.VerificationSource;
import cn.lili.modules.system.entity.vo.VerificationDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 验证码资源维护 业务层
 *
 * @author Chopper
 * @since 2020/11/17 3:44 下午
 */
public interface VerificationSourceService extends IService<VerificationSource> {

    /**
     * 缓存
     */
    String VERIFICATION_CACHE = CachePrefix.VERIFICATION.getPrefix();


    /**
     * 初始化缓存
     *
     * @return
     */
    VerificationDTO initCache();

    /**
     * 获取验证缓存
     *
     * @return 验证码
     */
    VerificationDTO getVerificationCache();
}