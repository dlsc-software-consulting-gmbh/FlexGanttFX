/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.msproject;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Programmatic factory for the built-in sample {@link ProjectFile} instances
 * shown in the MSProject demo selector.
 *
 * <p>Each project is generated fresh on every call so the Gantt chart always
 * receives a clean object graph.  Dates are relative to a fixed anchor
 * (2025-01-06, a Monday) so the data always looks realistic regardless of
 * when the demo is run.
 */
public final class SampleProjectFactory {

    /** All available sample projects in display order. */
    public static final List<SampleProject> ALL = List.of(
            new SampleProject("Software Release v2.0",   SampleProjectFactory::softwareRelease),
            new SampleProject("Office Renovation",        SampleProjectFactory::officeRenovation),
            new SampleProject("Product Launch Campaign",  SampleProjectFactory::productLaunch),
            new SampleProject("E-Commerce Website",       SampleProjectFactory::eCommerceWebsite),
            new SampleProject("Annual Tech Conference",   SampleProjectFactory::techConference)
    );

    private SampleProjectFactory() {}

    // =========================================================================
    // 1. Software Release v2.0
    // =========================================================================

    private static ProjectFile softwareRelease() {
        ProjectFile p = new ProjectFile();
        p.getProjectProperties().setName("Software Release v2.0");

        Date d = anchor(2025, Calendar.JANUARY, 6);

        // Phase 1 – Design (2 weeks)
        Task design = phase(p, "Design Phase", d, 10);
        Task ux         = leaf(design, "UX Research & Wireframes",   d,       5, 100);
        Task arch       = leaf(design, "Architecture Review",         plus(d,3), 5, 100);
        Task techSpec   = leaf(design, "Technical Specification",     plus(d,7), 3, 100);
        techSpec.addPredecessor(ux,   RelationType.FINISH_START, lag0());
        techSpec.addPredecessor(arch, RelationType.FINISH_START, lag0());

        // Phase 2 – Backend (3 weeks)
        Task backend = phase(p, "Backend Development", plus(d, 10), 15);
        Task db      = leaf(backend, "Database Schema Design",  plus(d,10), 5,  100);
        Task api     = leaf(backend, "REST API Implementation", plus(d,15), 7,   80);
        Task logic   = leaf(backend, "Business Logic Layer",    plus(d,17), 5,   60);
        Task intTest = leaf(backend, "Integration Tests",       plus(d,22), 3,   20);
        api.addPredecessor(db,    RelationType.FINISH_START, lag0());
        logic.addPredecessor(api, RelationType.FINISH_START, lag0());
        intTest.addPredecessor(logic, RelationType.FINISH_START, lag0());

        // Phase 3 – Frontend (2 weeks)
        Task frontend = phase(p, "Frontend Development", plus(d, 17), 10);
        Task compLib = leaf(frontend, "Component Library",    plus(d,17), 5,  80);
        Task pages   = leaf(frontend, "Page Implementation",  plus(d,20), 7,  50);
        Task e2e     = leaf(frontend, "End-to-End Tests",     plus(d,25), 3,   0);
        pages.addPredecessor(compLib, RelationType.FINISH_START, lag0());
        e2e.addPredecessor(pages, RelationType.FINISH_START, lag0());

        // Phase 4 – QA & Release (1.5 weeks)
        Task qa = phase(p, "QA & Release", plus(d, 27), 8);
        Task reg   = leaf(qa, "Regression Testing", plus(d,27), 3,  0);
        Task perf  = leaf(qa, "Performance Testing", plus(d,28), 2,  0);
        Task prod  = leaf(qa, "Production Deployment", plus(d,33), 2, 0);
        reg.addPredecessor(intTest, RelationType.FINISH_START, lag0());
        reg.addPredecessor(e2e,    RelationType.FINISH_START, lag0());
        perf.addPredecessor(reg,   RelationType.FINISH_START, lag0());
        prod.addPredecessor(perf,  RelationType.FINISH_START, lag0());

        return p;
    }

    // =========================================================================
    // 2. Office Renovation
    // =========================================================================

    private static ProjectFile officeRenovation() {
        ProjectFile p = new ProjectFile();
        p.getProjectProperties().setName("Office Renovation");

        Date d = anchor(2025, Calendar.MARCH, 3);

        // Phase 1 – Preparation (1 week)
        Task prep = phase(p, "Preparation", d, 5);
        Task design  = leaf(prep, "Design & Space Planning",    d,       3, 100);
        Task permits = leaf(prep, "Permit Applications",        plus(d,2), 3, 100);
        Task tender  = leaf(prep, "Contractor Selection",       plus(d,3), 2, 100);

        // Phase 2 – Structural Work (2 weeks)
        Task structural = phase(p, "Structural Work", plus(d, 5), 10);
        Task demo    = leaf(structural, "Demolition",           plus(d,5),  3, 100);
        Task electric= leaf(structural, "Electrical Wiring",    plus(d,7),  5,  70);
        Task plumbing= leaf(structural, "Plumbing",             plus(d,8),  4,  50);
        Task hvac    = leaf(structural, "HVAC Installation",    plus(d,10), 5,  20);
        demo.addPredecessor(permits, RelationType.FINISH_START, lag0());
        electric.addPredecessor(demo, RelationType.FINISH_START, lag0());
        plumbing.addPredecessor(demo, RelationType.FINISH_START, lag0());
        hvac.addPredecessor(electric, RelationType.FINISH_START, lag0());

        // Phase 3 – Finishes (2.5 weeks)
        Task finishes = phase(p, "Finishes", plus(d, 15), 13);
        Task drywall = leaf(finishes, "Drywall & Plastering",  plus(d,15), 5, 0);
        Task painting= leaf(finishes, "Painting",              plus(d,19), 4, 0);
        Task flooring= leaf(finishes, "Flooring",              plus(d,21), 5, 0);
        Task ceilings= leaf(finishes, "Suspended Ceilings",    plus(d,22), 4, 0);
        drywall.addPredecessor(plumbing, RelationType.FINISH_START, lag0());
        painting.addPredecessor(drywall, RelationType.FINISH_START, lag0());
        flooring.addPredecessor(painting,RelationType.FINISH_START, lag0());
        ceilings.addPredecessor(drywall, RelationType.FINISH_START, lag0());

        // Phase 4 – Fit-Out (1.5 weeks)
        Task fitout = phase(p, "Fit-Out", plus(d, 28), 7);
        Task furniture = leaf(fitout, "Furniture Installation", plus(d,28), 3, 0);
        Task it        = leaf(fitout, "IT Infrastructure",      plus(d,29), 4, 0);
        Task inspect   = leaf(fitout, "Final Inspection",       plus(d,33), 2, 0);
        furniture.addPredecessor(flooring, RelationType.FINISH_START, lag0());
        it.addPredecessor(hvac, RelationType.FINISH_START, lag0());
        inspect.addPredecessor(furniture, RelationType.FINISH_START, lag0());
        inspect.addPredecessor(it, RelationType.FINISH_START, lag0());

        return p;
    }

    // =========================================================================
    // 3. Product Launch Campaign
    // =========================================================================

    private static ProjectFile productLaunch() {
        ProjectFile p = new ProjectFile();
        p.getProjectProperties().setName("Product Launch Campaign");

        Date d = anchor(2025, Calendar.APRIL, 7);

        // Phase 1 – Research (1.5 weeks)
        Task research = phase(p, "Market Research", d, 7);
        Task compAnal = leaf(research, "Competitor Analysis",     d,       4, 100);
        Task custSurv = leaf(research, "Customer Surveys",        plus(d,2), 5, 100);
        Task findings = leaf(research, "Research Findings Report",plus(d,5), 2, 100);
        findings.addPredecessor(compAnal, RelationType.FINISH_START, lag0());
        findings.addPredecessor(custSurv, RelationType.FINISH_START, lag0());

        // Phase 2 – Branding & Creative (2 weeks)
        Task branding = phase(p, "Branding & Creative", plus(d, 7), 10);
        Task nameSlogan= leaf(branding, "Name & Slogan",           plus(d,7),  3,  80);
        Task visual    = leaf(branding, "Visual Identity Design",   plus(d,8),  6,  60);
        Task assets    = leaf(branding, "Marketing Asset Creation", plus(d,12), 5,  20);
        visual.addPredecessor(findings, RelationType.FINISH_START, lag0());
        assets.addPredecessor(visual,   RelationType.FINISH_START, lag0());

        // Phase 3 – Content & Channels (2 weeks)
        Task content = phase(p, "Content & Channels", plus(d, 14), 10);
        Task website = leaf(content, "Landing Page",              plus(d,14), 5,  0);
        Task social  = leaf(content, "Social Media Campaign",     plus(d,16), 7,  0);
        Task prKit   = leaf(content, "Press Kit & Media Outreach",plus(d,17), 5,  0);
        Task email   = leaf(content, "Email Campaign Setup",      plus(d,18), 4,  0);
        website.addPredecessor(assets, RelationType.FINISH_START, lag0());
        social.addPredecessor(assets,  RelationType.FINISH_START, lag0());

        // Phase 4 – Launch Event (1 week)
        Task launch = phase(p, "Launch Event", plus(d, 24), 5);
        Task venue  = leaf(launch, "Venue & Logistics",   plus(d,24), 3,  0);
        Task pressConf = leaf(launch, "Press Conference",  plus(d,27), 1,  0);
        Task goLive = leaf(launch, "Product Go-Live",      plus(d,28), 1,  0);
        pressConf.addPredecessor(venue,   RelationType.FINISH_START, lag0());
        pressConf.addPredecessor(prKit,   RelationType.FINISH_START, lag0());
        goLive.addPredecessor(email,      RelationType.FINISH_START, lag0());
        goLive.addPredecessor(pressConf,  RelationType.FINISH_START, lag0());

        return p;
    }

    // =========================================================================
    // 4. E-Commerce Website
    // =========================================================================

    private static ProjectFile eCommerceWebsite() {
        ProjectFile p = new ProjectFile();
        p.getProjectProperties().setName("E-Commerce Website");

        Date d = anchor(2025, Calendar.MAY, 5);

        // Phase 1 – UX (1.5 weeks)
        Task ux = phase(p, "UX Design", d, 7);
        Task userResearch = leaf(ux, "User Research",         d,       3, 100);
        Task infoArch     = leaf(ux, "Information Architecture", plus(d,2), 3, 100);
        Task wireframes   = leaf(ux, "Wireframes & Prototypes", plus(d,4), 4,  80);
        wireframes.addPredecessor(infoArch, RelationType.FINISH_START, lag0());

        // Phase 2 – Design (1.5 weeks)
        Task design = phase(p, "Visual Design", plus(d, 7), 7);
        Task styleGuide = leaf(design, "Style Guide",           plus(d,7),  3,  60);
        Task mockups    = leaf(design, "High-Fidelity Mockups", plus(d,9),  5,  30);
        Task assets2    = leaf(design, "Asset Export",          plus(d,12), 2,   0);
        styleGuide.addPredecessor(wireframes, RelationType.FINISH_START, lag0());
        mockups.addPredecessor(styleGuide, RelationType.FINISH_START, lag0());
        assets2.addPredecessor(mockups,    RelationType.FINISH_START, lag0());

        // Phase 3 – Development (3 weeks)
        Task dev = phase(p, "Development", plus(d, 14), 15);
        Task catalogue = leaf(dev, "Product Catalogue",    plus(d,14), 7, 0);
        Task cart      = leaf(dev, "Shopping Cart",        plus(d,17), 5, 0);
        Task payment   = leaf(dev, "Payment Integration",  plus(d,21), 5, 0);
        Task admin     = leaf(dev, "Admin Dashboard",      plus(d,18), 7, 0);
        catalogue.addPredecessor(assets2, RelationType.FINISH_START, lag0());
        cart.addPredecessor(catalogue, RelationType.FINISH_START, lag0());
        payment.addPredecessor(cart,   RelationType.FINISH_START, lag0());

        // Phase 4 – QA & Launch (1 week)
        Task qa = phase(p, "QA & Launch", plus(d, 29), 6);
        Task testing  = leaf(qa, "Functional Testing",  plus(d,29), 3, 0);
        Task seo      = leaf(qa, "SEO Optimisation",    plus(d,30), 2, 0);
        Task goLive2  = leaf(qa, "Go-Live & Monitoring",plus(d,33), 2, 0);
        testing.addPredecessor(payment, RelationType.FINISH_START, lag0());
        testing.addPredecessor(admin,   RelationType.FINISH_START, lag0());
        goLive2.addPredecessor(testing, RelationType.FINISH_START, lag0());
        goLive2.addPredecessor(seo,     RelationType.FINISH_START, lag0());

        return p;
    }

    // =========================================================================
    // 5. Annual Tech Conference
    // =========================================================================

    private static ProjectFile techConference() {
        ProjectFile p = new ProjectFile();
        p.getProjectProperties().setName("Annual Tech Conference");

        Date d = anchor(2025, Calendar.JUNE, 2);

        // Phase 1 – Strategic Planning (1 week)
        Task planning = phase(p, "Strategic Planning", d, 5);
        Task theme    = leaf(planning, "Theme & Topics Definition",d,       3, 100);
        Task budget   = leaf(planning, "Budget Planning",          plus(d,2), 3, 100);
        Task timeline2= leaf(planning, "Event Timeline",           plus(d,3), 2, 100);

        // Phase 2 – Venue & Logistics (2 weeks)
        Task venue = phase(p, "Venue & Logistics", plus(d, 5), 10);
        Task venueBook= leaf(venue, "Venue Selection & Booking", plus(d,5),  4,  90);
        Task catering = leaf(venue, "Catering Arrangements",     plus(d,8),  4,  50);
        Task av       = leaf(venue, "AV & Tech Equipment",       plus(d,9),  5,  30);
        Task signage  = leaf(venue, "Signage & Decoration",      plus(d,12), 3,   0);
        venueBook.addPredecessor(budget,   RelationType.FINISH_START, lag0());
        catering.addPredecessor(venueBook, RelationType.FINISH_START, lag0());

        // Phase 3 – Speakers & Content (2.5 weeks)
        Task speakers = phase(p, "Speakers & Content", plus(d, 7), 12);
        Task callPapers= leaf(speakers, "Call for Papers",         plus(d,7),  5, 80);
        Task speakerInv= leaf(speakers, "Keynote Speaker Invites", plus(d,8),  4, 70);
        Task agenda    = leaf(speakers, "Agenda Finalisation",     plus(d,15), 4,  0);
        Task workshops = leaf(speakers, "Workshop Programme",      plus(d,14), 5,  0);
        agenda.addPredecessor(callPapers,  RelationType.FINISH_START, lag0());
        agenda.addPredecessor(speakerInv, RelationType.FINISH_START, lag0());

        // Phase 4 – Marketing & Registration (2 weeks)
        Task marketing = phase(p, "Marketing & Registration", plus(d, 12), 10);
        Task website2  = leaf(marketing, "Conference Website",      plus(d,12), 5, 0);
        Task ticketing = leaf(marketing, "Ticketing System",        plus(d,14), 4, 0);
        Task socialMkt = leaf(marketing, "Social Media Campaign",   plus(d,15), 7, 0);
        Task sponsorship= leaf(marketing,"Sponsorship Packages",    plus(d,13), 5, 0);
        website2.addPredecessor(agenda,    RelationType.FINISH_START, lag0());
        ticketing.addPredecessor(website2, RelationType.FINISH_START, lag0());

        // Phase 5 – Day-Of (2 days)
        Task event = phase(p, "Event Execution", plus(d, 28), 2);
        Task setup    = leaf(event, "Venue Setup & Rehearsal", plus(d,28), 1, 0);
        Task confDay  = leaf(event, "Conference Day",          plus(d,29), 1, 0);
        setup.addPredecessor(signage,   RelationType.FINISH_START, lag0());
        setup.addPredecessor(av,        RelationType.FINISH_START, lag0());
        confDay.addPredecessor(setup,   RelationType.FINISH_START, lag0());

        return p;
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static Date anchor(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 8, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private static Date plus(Date base, int calendarDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.DAY_OF_MONTH, calendarDays);
        return c.getTime();
    }

    private static Duration lag0() {
        return Duration.getInstance(0, TimeUnit.DAYS);
    }

    /** Add a summary/phase task directly on the project (top-level). */
    private static Task phase(ProjectFile project, String name, Date start, int durationDays) {
        Task t = project.addTask();
        t.setName(name);
        t.setStart(start);
        t.setFinish(plus(start, durationDays));
        t.setDuration(Duration.getInstance(durationDays, TimeUnit.DAYS));
        return t;
    }

    /** Add a leaf task as a child of a summary task. */
    private static Task leaf(Task parent, String name, Date start, int durationDays, double pct) {
        Task t = parent.addTask();
        t.setName(name);
        t.setStart(start);
        t.setFinish(plus(start, durationDays));
        t.setDuration(Duration.getInstance(durationDays, TimeUnit.DAYS));
        t.setPercentageComplete(pct);
        return t;
    }
}
