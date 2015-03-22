package handlers;

import org.androidannotations.annotations.EBean;

import models.User;

@EBean(scope = EBean.Scope.Singleton)
public class HandlerUser {
    private User mUser;

    public User getHim() {
        return mUser;
    }

    public void setHim(User user) {
        mUser = user;
    }
}
