package cn.nispring.rail12306.service;

import cn.nispring.rail12306.mapper.PassengerMapper;
import cn.nispring.rail12306.model.IdType;
import cn.nispring.rail12306.model.PassengerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class IdService {

    private final IdWorker worker;

    public IdService(IdWorker worker) {
        this.worker = worker;
    }

    /// 发起身份核验。核验任务交给后台虚拟线程执行（见 IdWorker），方法本身立即返回，不阻塞调用方。
    /// 返回 true 表示核验任务已提交。
    public void verify(Long userId, IdType idType, String idNo) {
        worker.verify(userId, idType, idNo);
    }

    // @Async 基于代理生效，必须由另一个 bean 调用，所以拆成嵌套组件而不是 IdService 自己的方法。
    @Component
    public static class IdWorker {

        private static final Logger log = LoggerFactory.getLogger(IdWorker.class);
        private final PassengerMapper passengerMapper;

        public IdWorker(PassengerMapper passengerMapper) {
            this.passengerMapper = passengerMapper;
        }

        // 模拟外部核验接口的耗时。
        @Async
        public void verify(Long userId, IdType idType, String idNo) {
            // 模拟验证接口
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                log.warn("Identity verification interrupted, idType={}", idType);
                // TODO 重新加入排队队列
                Thread.currentThread().interrupt();
            }

            if (idNo.startsWith("114514")) {
                passengerMapper.updateStatus(userId, idType, idNo, PassengerStatus.REJECTED);
            } else {
                passengerMapper.updateStatus(userId, idType, idNo, PassengerStatus.PASSED);
            }
        }
    }
}
