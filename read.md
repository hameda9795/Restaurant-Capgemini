package com.narnia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.narnia.model.Visitor;
import com.narnia.repository.VisitorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

	// حداکثر تعداد بازدیدکنندگان مجاز در نارنیا
	private static final int MAX_VISITORS = 5;

	@Autowired
	private VisitorRepository visitorRepository;

	// ثبت بازدیدکننده جدید
	public Visitor registerVisitor(Visitor visitor) {
		visitor.setInNarnia(false);  // بازدیدکننده در ابتدا در نارنیا نیست
		return visitorRepository.save(visitor);
	}

	// گرفتن لیست همه بازدیدکنندگان
	public List<Visitor> getAllVisitors() {
		return visitorRepository.findAll();
	}

	// گرفتن لیست بازدیدکنندگان حاضر در نارنیا
	public List<Visitor> getVisitorsInNarnia() {
		return visitorRepository.findByInNarniaTrue();
	}

	// گرفتن لیست بازدیدکنندگان خارج از نارنیا
	public List<Visitor> getVisitorsOutsideNarnia() {
		return visitorRepository.findByInNarniaFalse();
	}

	// ورود بازدیدکننده به نارنیا
	public boolean enterNarnia(Long visitorId) {
		// بررسی ظرفیت نارنیا - استفاده از متد جدید
		long currentVisitorsCount = visitorRepository.countByInNarniaTrue();

		if (currentVisitorsCount >= MAX_VISITORS) {
			return false;  // نارنیا پر است
		}

		// پیدا کردن بازدیدکننده
		Optional<Visitor> visitorOpt = visitorRepository.findById(visitorId);

		if (visitorOpt.isPresent()) {
			Visitor visitor = visitorOpt.get();

			// اگر قبلاً وارد نارنیا شده، ورود مجدد ممکن نیست
			if (visitor.isInNarnia()) {
				return false;
			}

			visitor.setInNarnia(true);
			visitorRepository.save(visitor);
			return true;
		}

		return false;
	}

	// خروج بازدیدکننده از نارنیا
	public boolean exitNarnia(Long visitorId) {
		Optional<Visitor> visitorOpt = visitorRepository.findById(visitorId);

		if (visitorOpt.isPresent()) {
			Visitor visitor = visitorOpt.get();
			visitor.setInNarnia(false);
			visitorRepository.save(visitor);
			return true;
		}

		return false;
	}

	// حذف بازدیدکننده
	public void deleteVisitor(Long visitorId) {
		visitorRepository.deleteById(visitorId);
	}

	// ویرایش اطلاعات بازدیدکننده
	public Visitor updateVisitor(Long id, Visitor visitorDetails) {
		Optional<Visitor> visitorOpt = visitorRepository.findById(id);

		if (visitorOpt.isPresent()) {
			Visitor visitor = visitorOpt.get();
			visitor.setFirstName(visitorDetails.getFirstName());
			visitor.setLastName(visitorDetails.getLastName());
			visitor.setAge(visitorDetails.getAge());
			visitor.setCity(visitorDetails.getCity());
			// وضعیت حضور در نارنیا را تغییر نمی‌دهیم
			return visitorRepository.save(visitor);
		}

		return null;
	}
}

package com.narnia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.narnia.model.Visitor;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

	// بازدیدکنندگان داخل نارنیا
	List<Visitor> findByInNarniaTrue();

	// بازدیدکنندگان خارج نارنیا
	List<Visitor> findByInNarniaFalse();

	// شمارش تعداد بازدیدکنندگان در نارنیا (بدون استفاده از کوئری)
	long countByInNarniaTrue();
}






package com.narnia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.narnia.model.Visitor;
import com.narnia.service.VisitorService;

@Controller
public class VisitorController {

    @Autowired
    private VisitorService visitorService;
    
    // صفحه اصلی - همه عملیات از اینجا انجام می‌شود
    @GetMapping("/")
    public String home(Model model) {
        // اطلاعات مورد نیاز برای نمایش صفحه
        model.addAttribute("visitors", visitorService.getAllVisitors());
        model.addAttribute("visitorsInNarnia", visitorService.getVisitorsInNarnia());
        model.addAttribute("newVisitor", new Visitor());
        return "index";
    }
    
    // ثبت بازدیدکننده جدید
    @PostMapping("/register")
    public String registerVisitor(@ModelAttribute Visitor visitor) {
        visitorService.registerVisitor(visitor);
        return "redirect:/";
    }
    
    // ورود به نارنیا
    @GetMapping("/enter/{id}")
    public String enterNarnia(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        boolean success = visitorService.enterNarnia(id);
        
        if (!success) {
            redirectAttrs.addFlashAttribute("errorMessage", "نارنیا پر است!");
        }
        
        return "redirect:/";
    }
    
    // خروج از نارنیا
    @GetMapping("/exit/{id}")
    public String exitNarnia(@PathVariable Long id) {
        visitorService.exitNarnia(id);
        return "redirect:/";
    }
    
    // حذف بازدیدکننده
    @GetMapping("/delete/{id}")
    public String deleteVisitor(@PathVariable Long id) {
        visitorService.deleteVisitor(id);
        return "redirect:/";
    }
}


مشکلات اصلی:
1. در فایل VisitorController.java:

متد exitNarnia دو بار تعریف شده است (یکی با long و دیگری با Long)
در متد registerVisitor، مقدار بازگشتی اشتباه است: return "/redirect" باید به return "redirect:/" تغییر کند
فراخوانی‌های استاتیک مثل NarniaService.enterNarnia() در حالی که سرویس از طریق @Autowired به صورت غیراستاتیک تزریق شده است

2. در فایل NarniaService.java:

ریپوزیتوری به صورت استاتیک تعریف شده: private static VisitorRepository visitorRepository
در متد enterNarnia، شرط نادرست است: currentVisitorcount <= Max_Visitor باید >= باشد
متد countByInNarnia() در ریپوزیتوری باید countByInNarniaTrue() باشد

3. در فایل Visitor.java:

فیلدها name و lastname هستند اما در HTML از firstName و lastName استفاده شده
در سازنده، مقداردهی this.isNarnia = isNarnia; اشتباه است چون پارامتر isNarnia در لیست پارامترها وجود ندارد

4. در فایل VisitorRepository.java:

متدها به InNarnia اشاره می‌کنند اما در کلاس Visitor فیلد isNarnia است

5. در فایل index.html:

به visitor.firstName و visitor.lastName اشاره می‌کند اما کلاس Visitor فیلدهای name و lastname دارد
به outsideVisitors و narniaVisitors اشاره می‌کند اما کنترلر از visitors و visitorInNarnia استفاده می‌کند

راه‌حل‌های پیشنهادی:

نام‌گذاری یکسان:

فیلدهای Visitor را به firstName و lastName تغییر دهید تا با HTML مطابقت داشته باشد
از inNarnia به جای isNarnia استفاده کنید (قرارداد جاوا)
اسامی متغیرها در کنترلر را با HTML هماهنگ کنید


اصلاح خطاهای منطقی:

شرط در enterNarnia را اصلاح کنید
متد تکراری exitNarnia را حذف کنید
دستور return در registerVisitor را تصحیح کنید


رفع مشکلات استاتیک/غیراستاتیک:

متدهای سرویس را غیراستاتیک کنید و از نمونه تزریق شده استفاده کنید