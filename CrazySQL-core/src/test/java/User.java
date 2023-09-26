import com.crazy.sql.core.annotation.Table;
import com.crazy.sql.core.annotation.TableField;
import com.crazy.sql.core.annotation.TableId;
import com.crazy.sql.core.enums.PrimaryMode;

import java.util.Date;

/**
 * @author Akiba no ichiichiyoha
 */
@Table("tb_user")
public  class User {
    @TableId(PrimaryMode.JDBC_AUTO)
    public int userId;
    public String loginName;
    public String password;
    private String userName;
    private String roleName;
    private String rights;
    private String iconPath;
    private boolean sex;
    @TableField(value = "last_time")
    private Date lastTime;

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", rights='" + rights + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", sex=" + sex +
                ", lastTime=" + lastTime +
                '}';
    }
}