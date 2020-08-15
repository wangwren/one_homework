package com.lagou.edu.service.impl;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;

/**
 * @author 应癫
 */
@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private AccountDao JdbcAccountDaoImpl;

    @Override
    @Transactional
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = JdbcAccountDaoImpl.queryAccountByCardNo(fromCardNo);
        Account to = JdbcAccountDaoImpl.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney()-money);
        to.setMoney(to.getMoney()+money);

        JdbcAccountDaoImpl.updateAccountByCardNo(to);
        int c = 1/0;
        JdbcAccountDaoImpl.updateAccountByCardNo(from);
    }
}
