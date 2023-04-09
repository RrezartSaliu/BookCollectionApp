package com.example.bookcollection.web;

import com.example.bookcollection.model.Book;
import com.example.bookcollection.model.BookUser;
import com.example.bookcollection.model.Exceptions.InvalidArgumentsException;
import com.example.bookcollection.model.Exceptions.InvalidUserCredentialsException;
import com.example.bookcollection.model.UserType;
import com.example.bookcollection.service.AuthService;
import com.example.bookcollection.service.BookService;
import com.example.bookcollection.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController {
    private final BookService bookService;
    private final UserService userService;
    private final AuthService authService;

    public WebController(BookService bookService, UserService userService, AuthService authService) {
        this.bookService = bookService;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping({"/", "/home"})
    public String showHomepage(Model model){
        model.addAttribute("books", bookService.listTop12());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken)
            model.addAttribute("userAnonymous", true);
        else model.addAttribute("hasUser", true);
        return "index";
    }

    @GetMapping("/home/bookStore")
    public String showBookStore(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String author,
                                @RequestParam(required = false) String category,
                                @RequestParam(required = false) Integer page,
                                Model model){
        int totalPages = (int) Math.ceil(bookService.listAll().size()/400.0);
        if(page != null) {
            if (page >= totalPages || page <= 0)
                page = 1;
        }
        else page = 1;
        int current = page;
        List<Integer> pageRange= new ArrayList<>();
        int start, end;
        Boolean didntEnter = true;
        for(start = 1, end = 10; end<=totalPages; start+=10, end+=10){
            if(current >=start && current <=end) {
                didntEnter = false;
                pageRange = IntStream.range(start, end + 1).boxed().collect(Collectors.toList());
            }
        }
        if(didntEnter){
            pageRange = IntStream.range(start, totalPages+1).boxed().collect(Collectors.toList());
        }
        model.addAttribute("current", current);
        model.addAttribute("pageRange", pageRange);
        if(name == null && author == null && category == null) {
            if (page == null || page == 1 || page < 0) {
                model.addAttribute("books", bookService.listAll().subList(0, 400));
            }
            else {
                try {
                    model.addAttribute("books", bookService.listAll().subList((page - 1) * 400, ((page - 1) * 400) + 400));
                }
                catch (IndexOutOfBoundsException e){
                    try {
                        model.addAttribute("books", bookService.listAll().subList((page-1)*400, bookService.listAll().size()));
                    }
                    catch (IllegalArgumentException ie){
                        model.addAttribute("books", bookService.listAll().subList(0,400));
                    }
                }
            }
        }
        else {
            model.addAttribute("hasParameter", true);
            model.addAttribute("books", bookService.filter(author, category, name));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        model.addAttribute("user", bookUser);
        if (authentication.isAuthenticated())
            model.addAttribute("userAnonymous", true);

        return "bookStore";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, Model model){
        BookUser user = null;
        try {
            user = this.authService.login(request.getParameter("username"), request.getParameter("password"));
            request.getSession().setAttribute("user", user);
            return "redirect:/home";
        }
        catch (InvalidUserCredentialsException e){
            model.addAttribute("hasError", true);
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String getRegisterPage(@RequestParam(required = false) String error, Model model){
        if(error != null && !error.isEmpty()){
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        return "register";
    }

    @PostMapping("register")
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam String firstname, @RequestParam String lastname, @RequestParam UserType role){
        try {
            this.userService.register(firstname, lastname, email, password, role);
            return "redirect:/login";
        }catch (InvalidArgumentsException e){
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout (HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/login";
    }

    @PostMapping("/home/addToCollection/{id}")
    public String addBookToCollection(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        Book book = bookService.findById(id);
        userService.addBookToCollection(book, bookUser.getId());
        return "redirect:/home/bookStore";
    }

    @PostMapping("/home/removeFromCollection/{id}")
    public String removeBookFromCollection(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        Book book = bookService.findById(id);
        userService.removeBookFromMyCollection(book, bookUser.getId());
        return "redirect:/myCollection";
    }

    @PostMapping("/home/addBookToWishList/{id}")
    public String addBookToWishList(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        Book book = bookService.findById(id);
        userService.addBookToWishList(book, bookUser.getId());
        return "redirect:/home/bookStore";
    }

    @PostMapping("/home/removeFromWishList/{id}")
    public String removeBookFromWishList(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        Book book = bookService.findById(id);
        userService.removeFromWishList(book, bookUser.getId());
        return "redirect:/MyWishList";
    }

    @GetMapping("/myCollection")
    public String showMyCollectionPage(Model model, @RequestParam(required = false) Integer page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        if (page == null || page == 1 || page < 0) {
            if(bookUser.getMyCollection().size() < 100) {
                model.addAttribute("books", bookUser.getMyCollection().subList(0, bookUser.getMyCollection().size()));
                return "myCollection";
            }
            else {
                model.addAttribute("books", bookUser.getMyCollection().subList(0, bookUser.getMyCollection().size()));
            }
        }
        else {
            try {
                model.addAttribute("books", bookUser.getMyCollection().subList((page - 1) * 100, ((page - 1) * 100) + 100));
            }
            catch (IndexOutOfBoundsException e){
                try {
                    model.addAttribute("books", bookUser.getMyCollection().subList((page-1)*100, bookUser.getMyCollection().size()));
                }
                catch (IllegalArgumentException ie){
                    model.addAttribute("books", bookUser.getMyCollection().subList(0, 100));
                }
            }
        }
        return "myCollection";
    }

    @GetMapping("/MyWishList")
    public String showMyWishListPage(Model model, @RequestParam(required = false) Integer page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        if (page == null || page == 1 || page < 0){
            if (bookUser.getMyWishList().size() < 100){
                model.addAttribute("books", bookUser.getMyWishList().subList(0, bookUser.getMyWishList().size()));
                return "myWishList";
            }
            else {
                model.addAttribute("books", bookUser.getMyWishList().subList(0, 100));
            }
        }
        else {
            try {
                model.addAttribute("books", bookUser.getMyCollection().subList((page - 1) * 100, ((page - 1) * 100) + 100));
            }
            catch (IndexOutOfBoundsException e){
                try {
                    model.addAttribute("books", bookUser.getMyWishList().subList((page-1)*100, bookUser.getMyWishList().size()));
                }
                catch (IllegalArgumentException ie){
                    model.addAttribute("books", bookUser.getMyWishList().subList(0, 100));
                }
            }
        }
        return "myWishList";
    }
    @PostMapping("/home/likeBook/{id}")
    public String likeBook(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        BookUser bookUser = userService.findByEmail(userEmail);
        Book book = bookService.findById(id);
        userService.likeBook(book, bookUser.getId());
        return "redirect:/home/bookStore";
    }

}
