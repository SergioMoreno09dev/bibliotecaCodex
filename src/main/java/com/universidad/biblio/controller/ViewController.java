package com.universidad.biblio.controller;

import com.universidad.biblio.model.Author;
import com.universidad.biblio.model.Book;
import com.universidad.biblio.model.Category;
import com.universidad.biblio.model.Permission;
import com.universidad.biblio.model.Publisher;
import com.universidad.biblio.model.Report;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.UserRepository;
import com.universidad.biblio.service.AuthorService;
import com.universidad.biblio.service.BookService;
import com.universidad.biblio.service.CategoryService;
import com.universidad.biblio.service.FineService;
import com.universidad.biblio.service.LoanService;
import com.universidad.biblio.service.MensajeService;
import com.universidad.biblio.service.NotificationService;
import com.universidad.biblio.service.OrderService;
import com.universidad.biblio.service.PermissionService;
import com.universidad.biblio.service.PublisherService;
import com.universidad.biblio.service.ReportService;
import com.universidad.biblio.service.ReviewService;
import com.universidad.biblio.service.UserServi;
import com.universidad.biblio.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class ViewController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final LoanService loanService;
    private final OrderService orderService;
    private final FineService fineService;
    private final UserServi userService;
    private final ReviewService reviewService;
    private final MensajeService mensajeService;
    private final NotificationService notificationService;
    private final ReportService reportService;
    private final PermissionService permissionService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public ViewController(BookService bookService, AuthorService authorService, CategoryService categoryService,
                          PublisherService publisherService, LoanService loanService, OrderService orderService,
                          FineService fineService, UserServi userService, ReviewService reviewService,
                          MensajeService mensajeService, NotificationService notificationService, ReportService reportService,
                          PermissionService permissionService, AuditLogService auditLogService,
                          UserRepository userRepository) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
        this.loanService = loanService;
        this.orderService = orderService;
        this.fineService = fineService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.mensajeService = mensajeService;
        this.notificationService = notificationService;
        this.reportService = reportService;
        this.permissionService = permissionService;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @GetMapping({"/admin/books", "/admin/books/edit"})
    public String books(@RequestParam(required = false) String term,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String isbn,
                        Model model) {
        model.addAttribute("books", bookService.list(term, category));
        model.addAttribute("book", isbn == null || isbn.isBlank() ? new Book() : bookService.find(isbn));
        addBookCatalogs(model);
        return "admin/books";
    }

    @PostMapping("/admin/books/save")
    public String saveBook(@RequestParam String isbn,
                           @RequestParam String title,
                           @RequestParam int stock,
                           @RequestParam String language,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date year,
                           @RequestParam(required = false) String type,
                           @RequestParam(required = false) String cantPage,
                           @RequestParam(required = false) Long publisherId,
                           @RequestParam(required = false) List<Long> authorIds,
                           @RequestParam(required = false) List<Long> categoryIds,
                           RedirectAttributes redirectAttributes) {
        try {
            Book book = new Book();
            book.setIsbn(isbn);
            book.setTitle(title);
            book.setStock(stock);
            book.setLanguage(language);
            book.setYear(year);
            book.setType(type);
            book.setCantPage(cantPage);
            if (publisherId != null) {
                book.setPublisher(publisherService.find(publisherId));
            }
            if (authorIds != null) {
                book.setAuthor(authorIds.stream().map(authorService::find).toList());
            }
            if (categoryIds != null) {
                book.setCategory(categoryIds.stream().map(categoryService::find).toList());
            }
            bookService.save(book);
            flash(redirectAttributes, "Libro guardado correctamente.");
        } catch (RuntimeException ex) {
            flash(redirectAttributes, ex.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/admin/books/delete")
    public String deleteBook(@RequestParam String isbn, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Libro eliminado.", () -> bookService.delete(isbn), "/admin/books");
    }

    @GetMapping({"/admin/authors", "/admin/authors/edit"})
    public String authors(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("authors", authorService.list());
        model.addAttribute("author", id == null ? new Author() : authorService.find(id));
        return "admin/authors";
    }

    @PostMapping("/admin/authors/save")
    public String saveAuthor(@RequestParam(required = false) Long id, Author author, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Autor guardado.", () -> {
            if (id == null) {
                authorService.save(author);
            } else {
                authorService.update(id, author);
            }
        }, "/admin/authors");
    }

    @PostMapping("/admin/authors/delete")
    public String deleteAuthor(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Autor eliminado.", () -> authorService.delete(id), "/admin/authors");
    }

    @GetMapping({"/admin/categories", "/admin/categories/edit"})
    public String categories(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("categories", categoryService.list());
        model.addAttribute("category", id == null ? new Category() : categoryService.find(id));
        return "admin/categories";
    }

    @PostMapping("/admin/categories/save")
    public String saveCategory(@RequestParam(required = false) Long id, Category category,
                               RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Categoria guardada.", () -> {
            if (id == null) {
                categoryService.save(category);
            } else {
                categoryService.update(id, category);
            }
        }, "/admin/categories");
    }

    @PostMapping("/admin/categories/delete")
    public String deleteCategory(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Categoria eliminada.", () -> categoryService.delete(id), "/admin/categories");
    }

    @GetMapping({"/admin/publishers", "/admin/publishers/edit"})
    public String publishers(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("publishers", publisherService.list());
        model.addAttribute("publisher", id == null ? new Publisher() : publisherService.find(id));
        return "admin/publishers";
    }

    @PostMapping("/admin/publishers/save")
    public String savePublisher(@RequestParam(required = false) Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) String country,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date foundingYear,
                                RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Editorial guardada.", () -> {
            Publisher publisher = new Publisher(name, country, foundingYear);
            if (id == null) {
                publisherService.save(publisher);
            } else {
                publisherService.update(id, publisher);
            }
        }, "/admin/publishers");
    }

    @PostMapping("/admin/publishers/delete")
    public String deletePublisher(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Editorial eliminada.", () -> publisherService.delete(id), "/admin/publishers");
    }

    @GetMapping({"/admin/loans", "/admin/loans/extend"})
    public String loans(Model model) {
        loanService.markExpiredLoans();
        model.addAttribute("loans", loanService.list());
        model.addAttribute("users", userService.list());
        model.addAttribute("books", bookService.list(null, null));
        return "admin/loans";
    }

    @PostMapping("/admin/loans/create")
    public String createLoan(@RequestParam int userId,
                             @RequestParam String isbn,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date returnDate,
                             RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Prestamo creado.", () -> loanService.create(userId, isbn, returnDate), "/admin/loans");
    }

    @PostMapping("/admin/loans/close")
    public String closeLoan(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Prestamo cerrado.", () -> loanService.close(id), "/admin/loans");
    }

    @PostMapping({"/admin/loans/extend", "/reader/loans/extend"})
    public String extendLoan(@RequestParam Long id,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date returnDate,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        Date newReturnDate = returnDate == null ? new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000) : returnDate;
        String target = isReader(authentication) ? "/reader/loans" : "/admin/loans";
        return execute(redirectAttributes, "Prestamo extendido.", () -> loanService.extend(id, newReturnDate), target);
    }

    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.list());
        model.addAttribute("users", userService.list());
        model.addAttribute("books", bookService.list(null, null));
        return "admin/orders";
    }

    @PostMapping({"/admin/orders/create", "/reader/orders/create"})
    public String createOrder(@RequestParam(required = false) Integer userId,
                              @RequestParam String isbn,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        User user = userId == null ? currentUser(authentication) : userService.find(userId);
        String target = isReader(authentication) ? "/reader/orders" : "/admin/orders";
        return execute(redirectAttributes, "Reserva creada.", () -> orderService.create(user.getId(), isbn), target);
    }

    @PostMapping({"/admin/orders/approve"})
    public String approveOrder(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Reserva aprobada.", () -> orderService.approve(id), "/admin/orders");
    }

    @PostMapping({"/admin/orders/cancel", "/reader/orders/cancel"})
    public String cancelOrder(@RequestParam Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        String target = isReader(authentication) ? "/reader/orders" : "/admin/orders";
        return execute(redirectAttributes, "Reserva cancelada.", () -> orderService.cancel(id), target);
    }

    @GetMapping("/admin/fines")
    public String fines(Model model) {
        model.addAttribute("fines", fineService.list());
        model.addAttribute("loans", loanService.list());
        return "admin/fines";
    }

    @PostMapping("/admin/fines/generate")
    public String generateFine(@RequestParam Long loanId,
                               @RequestParam double amount,
                               RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Multa generada.", () -> fineService.generate(loanId, amount), "/admin/fines");
    }

    @PostMapping({"/admin/fines/pay", "/reader/fines/pay"})
    public String payFine(@RequestParam Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        String target = isReader(authentication) ? "/reader/fines" : "/admin/fines";
        return execute(redirectAttributes, "Multa pagada.", () -> fineService.pay(id), target);
    }

    @GetMapping({"/admin/users", "/admin/users/edit"})
    public String users(@RequestParam(required = false) Integer id, Model model) {
        model.addAttribute("users", userService.list());
        model.addAttribute("user", id == null ? new User() : userService.find(id));
        return "admin/users";
    }

    @PostMapping("/admin/users/save")
    public String saveUser(@RequestParam(required = false) Integer id, User user, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Usuario guardado.", () -> {
            if (id == null) {
                userService.register(user);
            } else {
                userService.update(id, user);
            }
        }, "/admin/users");
    }

    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam int id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Usuario eliminado.", () -> userService.delete(id), "/admin/users");
    }

    @GetMapping("/admin/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewService.list());
        return "admin/reviews";
    }

    @PostMapping("/admin/reviews/delete")
    public String deleteReview(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Resena eliminada.", () -> reviewService.delete(id), "/admin/reviews");
    }

    @GetMapping("/admin/notifications")
    public String notifications(Model model) {
        model.addAttribute("notifications", notificationService.list());
        model.addAttribute("users", userService.list());
        return "admin/notifications";
    }

    @GetMapping("/admin/mensajes")
    public String adminMensajes(Model model, Authentication authentication) {
        addMensajesModel(model, authentication);
        return "admin/mensajes";
    }

    @PostMapping("/admin/mensajes/send")
    public String sendAdminMensaje(@RequestParam String destinatario,
                                   @RequestParam String asunto,
                                   @RequestParam String contenido,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {
        return execute(redirectAttributes, "Mensaje enviado.",
                () -> mensajeService.enviar(authentication.getName(), destinatario, asunto, contenido),
                "/admin/mensajes");
    }

    @PostMapping("/admin/mensajes/read")
    public String markAdminMensajeRead(@RequestParam Long id,
                                       RedirectAttributes redirectAttributes,
                                       Authentication authentication) {
        return execute(redirectAttributes, "Mensaje marcado como leido.",
                () -> mensajeService.marcarComoLeido(id, authentication.getName())
                        .orElseThrow(() -> new RuntimeException("Mensaje no encontrado")),
                "/admin/mensajes");
    }

    @PostMapping("/admin/notifications/send")
    public String sendNotification(@RequestParam int userId,
                                   @RequestParam String message,
                                   @RequestParam String type,
                                   RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Notificacion enviada.", () -> notificationService.send(userId, message, type), "/admin/notifications");
    }

    @GetMapping("/admin/reports")
    public String reports(Model model) {
        model.addAttribute("reports", reportService.list());
        model.addAttribute("exportRequests", reportService.listExports());
        model.addAttribute("report", new Report());
        return "admin/reports";
    }

    @PostMapping("/admin/reports/save")
    public String saveReport(Report report, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Reporte guardado.", () -> reportService.save(report), "/admin/reports");
    }

    @PostMapping("/admin/reports/summary")
    public String generateSummary(RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Resumen generado.", reportService::generateSummary, "/admin/reports");
    }

    @PostMapping("/admin/reports/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam Long id,
                                               @RequestParam String format,
                                               @RequestParam(required = false) String filters,
                                               Authentication authentication) {
        User user = currentUser(authentication);
        Report report = reportService.find(id);
        String normalizedFormat = format == null ? "TXT" : format.trim().toUpperCase();
        String appliedFilters = filters == null ? "" : filters.trim();
        reportService.export(id, user.getId(), normalizedFormat, appliedFilters);

        byte[] body = renderReport(report, normalizedFormat, appliedFilters).getBytes(StandardCharsets.UTF_8);
        String extension = "CSV".equals(normalizedFormat) ? "csv" : "txt";
        MediaType mediaType = "CSV".equals(normalizedFormat)
                ? MediaType.parseMediaType("text/csv")
                : MediaType.TEXT_PLAIN;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("reporte-" + id + "." + extension, StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(body);
    }

    @GetMapping("/admin/permissions")
    public String permissions(@RequestParam(required = false) String role, Model model) {
        model.addAttribute("permissions", role == null || role.isBlank() ? permissionService.list() : permissionService.byRole(role));
        model.addAttribute("permission", new Permission());
        return "admin/permissions";
    }

    @PostMapping("/admin/permissions/save")
    public String savePermission(Permission permission, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Permiso guardado.", () -> permissionService.save(permission), "/admin/permissions");
    }

    @GetMapping("/admin/audit-logs")
    public String auditLogs(@RequestParam(required = false) Integer userId, Model model) {
        model.addAttribute("auditLogs", userId == null ? auditLogService.list() : auditLogService.byUser(userId));
        return "admin/audit-logs";
    }

    @GetMapping("/reader/catalog")
    public String catalog(@RequestParam(required = false) String term,
                          @RequestParam(required = false) String category,
                          Model model) {
        model.addAttribute("books", bookService.list(term, category));
        return "reader/catalog";
    }

    @GetMapping("/reader/loans")
    public String readerLoans(Model model, Authentication authentication) {
        User user = currentUser(authentication);
        loanService.markExpiredLoans();
        model.addAttribute("loans", user == null ? List.of() : loanService.byUser(user.getId()));
        return "reader/loans";
    }

    @GetMapping("/reader/orders")
    public String readerOrders(Model model, Authentication authentication) {
        User user = currentUser(authentication);
        model.addAttribute("orders", user == null ? List.of() : orderService.byUser(user.getId()));
        return "reader/orders";
    }

    @GetMapping("/reader/fines")
    public String readerFines(Model model, Authentication authentication) {
        User user = currentUser(authentication);
        model.addAttribute("fines", user == null ? List.of() : fineService.byUser(user.getId()));
        return "reader/fines";
    }

    @GetMapping("/reader/notifications")
    public String readerNotifications(Model model, Authentication authentication) {
        User user = currentUser(authentication);
        model.addAttribute("notifications", user == null ? List.of() : notificationService.byUser(user.getId()));
        return "reader/notifications";
    }

    @GetMapping("/reader/mensajes")
    public String readerMensajes(Model model, Authentication authentication) {
        addMensajesModel(model, authentication);
        return "reader/mensajes";
    }

    @PostMapping("/reader/mensajes/send")
    public String sendReaderMensaje(@RequestParam String destinatario,
                                    @RequestParam String asunto,
                                    @RequestParam String contenido,
                                    RedirectAttributes redirectAttributes,
                                    Authentication authentication) {
        return execute(redirectAttributes, "Mensaje enviado.",
                () -> mensajeService.enviar(authentication.getName(), destinatario, asunto, contenido),
                "/reader/mensajes");
    }

    @PostMapping("/reader/mensajes/read")
    public String markReaderMensajeRead(@RequestParam Long id,
                                        RedirectAttributes redirectAttributes,
                                        Authentication authentication) {
        return execute(redirectAttributes, "Mensaje marcado como leido.",
                () -> mensajeService.marcarComoLeido(id, authentication.getName())
                        .orElseThrow(() -> new RuntimeException("Mensaje no encontrado")),
                "/reader/mensajes");
    }

    @PostMapping("/reader/notifications/read")
    public String markNotificationRead(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        return execute(redirectAttributes, "Notificacion marcada como leida.", () -> notificationService.markAsRead(id), "/reader/notifications");
    }

    @GetMapping("/reader/reviews/new")
    public String newReview(@RequestParam String isbn, Model model) {
        model.addAttribute("isbn", isbn);
        return "reader/review-form";
    }

    @PostMapping("/reader/reviews/save")
    public String saveReaderReview(@RequestParam String isbn,
                                   @RequestParam int rating,
                                   @RequestParam String comment,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {
        User user = currentUser(authentication);
        return execute(redirectAttributes, "Resena publicada.", () -> reviewService.create(user.getId(), isbn, rating, comment), "/reader/catalog");
    }

    private void addBookCatalogs(Model model) {
        model.addAttribute("authors", authorService.list());
        model.addAttribute("categories", categoryService.list());
        model.addAttribute("publishers", publisherService.list());
    }

    private void addMensajesModel(Model model, Authentication authentication) {
        User user = currentUser(authentication);
        String email = authentication == null ? null : authentication.getName();
        model.addAttribute("entrada", user == null ? List.of() : mensajeService.bandejaEntrada(email));
        model.addAttribute("enviados", user == null ? List.of() : mensajeService.enviados(email));
        model.addAttribute("noLeidos", user == null ? 0 : mensajeService.contarNoLeidos(email));
        model.addAttribute("users", user == null
                ? userService.list()
                : userService.list().stream()
                .filter(item -> !Objects.equals(item.getId(), user.getId()))
                .toList());
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return userRepository.findAll().stream().findFirst().orElse(null);
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
    }

    private boolean isReader(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_LECTOR")
                        || authority.getAuthority().equals("ROLE_USER"));
    }

    private String execute(RedirectAttributes redirectAttributes, String success, Runnable action, String target) {
        try {
            action.run();
            flash(redirectAttributes, success);
        } catch (RuntimeException ex) {
            flash(redirectAttributes, ex.getMessage());
        }
        return "redirect:" + target;
    }

    private void flash(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("flash", message);
    }

    private String renderReport(Report report, String format, String filters) {
        String generatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if ("CSV".equals(format)) {
            return "campo,valor\n"
                    + "id," + csv(report.getId()) + "\n"
                    + "titulo," + csv(report.getTitle()) + "\n"
                    + "tipo," + csv(report.getType()) + "\n"
                    + "filtros," + csv(filters) + "\n"
                    + "fecha_exportacion," + csv(generatedAt) + "\n"
                    + "contenido," + csv(report.getContent()) + "\n";
        }

        return "Reporte: " + nullSafe(report.getTitle()) + "\n"
                + "Tipo: " + nullSafe(report.getType()) + "\n"
                + "Filtros aplicados: " + nullSafe(filters) + "\n"
                + "Fecha de exportacion: " + generatedAt + "\n\n"
                + nullSafe(report.getContent());
    }

    private String csv(Object value) {
        String text = nullSafe(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private String nullSafe(Object value) {
        return value == null ? "" : value.toString();
    }

}
