package com.example.SpringGroupBB.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

//@Component
@Service
public class ProjectProvide {
	
	@Autowired
	JavaMailSender mailSender;

	// 메일 보내기
	public String mailSend(
			String toMail,
			String title,
			String mailFlag
		) throws MessagingException {
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

		String content = "";
		messageHelper.setTo(toMail);
		messageHelper.setSubject(title);
		messageHelper.setText(content);
		// 메세지보관함에 저장되는 'content'변수안에 발신자의 필요한 정보를 추가로 담아준다.
		content = content.replace("\n", "<br>");
		content += "<br><hr><h3>?에서 보냅니다.</h3><hr>";
		content += "<font size='5' color='red'><b>"+mailFlag+"</b></font><hr>";
		content += "<p>방문하기 : <a href='http://49.142.157.251:9090/cjgreen'>springGroup</a></p>";
		content += "<hr>";
		messageHelper.setText(content, true);

/*		FileSystemResource file = new FileSystemResource(request.getSession().getServletContext().getRealPath("/resources/images/?.png"));
		messageHelper.addInline("?", file);*/
		
		mailSender.send(message);
		
		return "1";
	}


  public void writeFile(MultipartFile sFile, String sFileName, String realPath) throws IOException {
    FileOutputStream fos = new FileOutputStream(realPath + sFileName);

    if(sFile.getBytes().length != -1) {
      fos.write(sFile.getBytes());
    }
    fos.flush();
    fos.close();
  }
}
