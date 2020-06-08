package com.software.auth;

import java.util.List;

import com.software.model.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SessionFactory factory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserbyUsername(username);

        UserBuilder builder = null;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
            builder.roles(user.getPermission());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
        return builder.build();
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private User findUserbyUsername(String username) {
        Criteria cr = getSession().createCriteria(User.class);
        cr.add(Restrictions.eq("username", username));
        List<User> results = cr.list();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    private Session getSession() {
		Session session = factory.getCurrentSession();
		if (session == null) {
			session = factory.openSession();
		}
		return session;
	}
    
}