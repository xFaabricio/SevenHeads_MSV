package br.com.sevenheads.userService.domain.product.api.v1;

import br.com.sevenheads.userService.domain.entity.Product;
import br.com.sevenheads.userService.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    private final TemplateEngine templateEngine;

    @GetMapping("/{language}/{uuid}")
    public String getProduct(@PathVariable("language") String language, @PathVariable("uuid") String uuid) {
        Context context = new Context();
        Optional<Product> product = productRepository.findById(UUID.fromString(uuid));
        if(product.isPresent()) {
            product.get().setAccessCount(product.get().getAccessCount() + 1);
            productRepository.save(product.get());

            if (Boolean.TRUE.equals(product.get().getUseCustomRedirect())) {
                if(language.equals("pt")) {
                    context.setVariable("newPage", product.get().getCustomRedirectPT());
                } else if (language.equals("en")) {
                    context.setVariable("newPage", product.get().getCustomRedirectEN());
                }else{
                    context.setVariable("newPage", product.get().getCustomRedirect());
                }

                return templateEngine.process("redirect", context);
            }

            if (language.equals("pt")) {
                return product.get().getHtmlPT();
            } else if (language.equals("en")) {
                return product.get().getHtmlEN();
            }
            return product.get().getHtml();
        }else{
            return "Product not found";
        }
    }

    @GetMapping("/{uuid}")
    public String getProductDefault(@PathVariable("uuid") String uuid) {
        Optional<Product> product = productRepository.findById(UUID.fromString(uuid));
        if(product.isPresent()) {
            if (Boolean.TRUE.equals(product.get().getUseCustomRedirect())) {
                Context context = new Context();
                context.setVariable("newPage", product.get().getCustomRedirect());
                return templateEngine.process("redirect", context);
            }
            product.get().setAccessCount(product.get().getAccessCount() + 1);
            productRepository.save(product.get());
            return product.get().getHtml();
        }else{
            return "Product not found";
        }
    }

}
