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
		content += "<div style='width: 600px; margin: 0 auto; padding: 20px;"
            + "border: 1px solid #ddd; border-radius: 10px; font-family: Arial, sans-serif;'>";
    content += "<h2 style='text-align: center; color: #4CAF50; margin-top: 0;'>"
            + "이메일 인증</h2>";
    content += "<p style='font-size: 16px; color: #333; text-align: center;'>"
            + "아래 인증번호를 입력해주세요"
            + "</p>";
    content += "<div style='text-align: center; margin: 30px 0;'>";
    content += "<div style='font-size: 32px; font-weight: bold; color: #FF3B30;"
            + "background: #FFF4F4; padding: 15px 20px; border-radius: 8px;"
            + "display: inline-block; letter-spacing: 5px;'>"
            + mailFlag + "</div>";
    content += "<p style='font-size: 14px; color: #555; text-align: center;'>"
            + "인증번호는 3분 동안만 유효합니다."
            + "</p>";
    content += "<hr>";
    content += "</div>";
		messageHelper.setText(content, true);


		
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
