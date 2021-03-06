package top.cellargalaxy.securityandshirodemo.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import top.cellargalaxy.securityandshirodemo.model.SecurityUser;
import top.cellargalaxy.securityandshirodemo.service.SecurityService;

import java.util.UUID;

/**
 * shiro会在这个类里验证账号密码的正确性和获取账号的权限
 * <p>
 * 对于账号密码不对或者没有权限等情况的处理
 * 网上有些教程说“可以在这个类的方法里抛很多种异常，对应不同的情况，可以参考官方文档”
 * 我觉得这种说法有点误导人，让人以为抛了官方介绍的这些异常之后框架会帮我们处理好
 * 就像spring security那样会返回个403
 * 然后却没有，只有浏览器里的500
 * 并且实际上任何抛的异常ExceptionController都无法捕抓到
 *
 * @author cellargalaxy
 * @time 2018/8/1
 */
public class RealmImpl extends AuthorizingRealm {
	private final SecurityService securityService;

	public RealmImpl(SecurityService securityService) {
		this.securityService = securityService;
	}

	//在调用filter的getSubject(request, response).createToken(authenticationToken)方法来登录时
	//会调用这个方法，用来检查账号密码是否正确
	//而这个方法的入参应该就是login方法的入参
	//对比方法是：
	//他传入的authenticationToken对象封装了登录的账号密码
	//我们自己在authenticationToken对象里获取登录的账号
	//通过登录的账号来获取正确的密码
	//然后将账号和正确的密码封装在AuthenticationInfo对象里(最后那个getName()入参不知道啥含义)
	//返回给他进行对比
	//貌似返回的账户名可以任意，只要不为空即可，为空会报错：
	//java.lang.IllegalArgumentException: principal argument cannot be null.
	//而密码错误也会报错：
	//org.apache.shiro.authc.IncorrectCredentialsException...
	//综上所述，在这里我们要返回正确的账号密码，
	//没有报错则匹配，否则以报错的方式通知
	//虽然报错ExceptionController也捕抓不到
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		//登录的账号名
		String token = (String) authenticationToken.getPrincipal();

		//先尝试把username作为token来解析
		SecurityUser securityUser = securityService.checkToken(token);

		System.out.println("解析token: " + securityUser);

		if (securityUser != null) {
			return new SimpleAuthenticationInfo(token, token, getName());
		} else {
			//账号不存在的时候拿uuid来充数
			//这样的话，无论是账号不存在还是密码错误
			//都会报IncorrectCredentialsException异常
			//就可以在ExceptionController里统一处理了
			String string = UUID.randomUUID().toString();
			return new SimpleAuthenticationInfo(string, string, getName());
		}
	}

	//验证权限时会调用这个方法
	//入参principalCollection只是账户名
	//所以每次需要验证权限都得反复查
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		//SecurityUtils.getSubject()也可以获取当前会话的账号对象
		//但是在登录的时候本来就没有设置权限，所以也查获取不了权限
//		String username = SecurityUtils.getSubject().getPrincipal().toString();
		//获取token，我把token设置在账号名里
		String token = principalCollection.toString();
		SecurityUser securityUser = securityService.checkToken(token);

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		if (securityUser != null) {
			//这里就简单处理，把角色和权限等价了
			info.setRoles(securityUser.getPermissions());
			info.setStringPermissions(securityUser.getPermissions());
		}

		System.out.println("校验权限: " + securityUser);

		return info;
	}
}
