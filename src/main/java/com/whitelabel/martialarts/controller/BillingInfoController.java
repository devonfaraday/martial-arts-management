package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.BillingInfo;
import com.whitelabel.martialarts.service.service.BillingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/billing-info")
public class BillingInfoController {

    @Autowired
    private BillingInfoService billingInfoService;

    @GetMapping("/{studentId}")
    public String getBillingInfoForStudent(@PathVariable Long studentId, Model model) {
        BillingInfo billingInfo = billingInfoService.getBillingInfoByStudentId(studentId);
        model.addAttribute("billingInfo", billingInfo);
        return "billing_info/view_billing_info"; // Thymeleaf template
    }

    @GetMapping("/{studentId}/edit")
    public String editBillingInfoForm(@PathVariable Long studentId, Model model) {
        BillingInfo billingInfo = billingInfoService.getBillingInfoByStudentId(studentId);
        model.addAttribute("billingInfo", billingInfo);
        return "billing_info/edit_billing_info"; // Thymeleaf template
    }

    @PostMapping("/{studentId}/edit")
    public String updateBillingInfo(@PathVariable Long studentId, @ModelAttribute BillingInfo billingInfo) {
        billingInfoService.updateBillingInfo(studentId, billingInfo);
        return "redirect:/students/" + studentId; // Redirect to student's details page
    }
}
