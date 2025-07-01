(function(){
    // Data reused from previous plus for client side companies:
    const allCompanies = [
        {id: 1, name: 'Gimnasio Doga', address: 'Avenida Manuel Ricardo Trelles 776, CABA', city: 'Ciudad de Buenos Aires', type: 'Gimnasio', active: true, open: 'Lunes a Viernes 09:00 a 18:00hs'},
        {id: 2, name: 'Clínica SaludTotal', address: 'Barcelona', city: 'Barcelona', type: 'Clinica', active: true},
        {id: 3, name: 'Estética Belleza', address: 'Valencia',  city: 'Valencia',type: 'Estética', active: true},
        {id: 4, name: 'Consultorio Luna', address: 'Sevilla', city: 'Sevilla', type: 'Consultorio', active: true},
        {id: 5, name: 'Consultorio Sol', address: 'Madrid', city: 'Madrid', type: 'Consultorio', active: false},
        {id: 6, name: 'Spa Relax Barcelona', address: 'Barcelona', city: 'Barcelona', type: 'Estética', active: true},
        {id: 7, name: 'Gimnasio PowerFit', location: 'Valencia', city: 'Valencia', type: 'Gimnasio', active: true},
    ];

    // Client companies filtering state
    let clientFilteredCompanies = [...allCompanies];

    // Elements client companies
    const clientFilterLocation = document.getElementById('client-filter-location');
    const clientFilterType = document.getElementById('client-filter-type');
    const clientFilterCities = document.getElementById('client-filter-cities');
    const clientFilterActive = document.getElementById('client-filter-active');
    const clientCompaniesContainer = document.getElementById('client-companies-container');
    const clientCompanySearchInput = document.getElementById('client-company-search');
    const clientBtnClearFilters = document.getElementById('client-btnClearFilters');

    function populateClientFilterSelects() {
        const locations = [...new Set(allCompanies.map(c => c.address))].sort();
        const types = [...new Set(allCompanies.map(c => c.type))].sort();
        const cities = [...new Set(allCompanies.map(c => c.city))].sort();

        clientFilterLocation.innerHTML = '<option value="">Todas</option>';
        locations.forEach(loc => {
            const opt = document.createElement('option');
            opt.value = loc;
            opt.textContent = loc;
            clientFilterLocation.appendChild(opt);
        });

        clientFilterType.innerHTML = '<option value="">Todos</option>';
        types.forEach(type => {
            const opt = document.createElement('option');
            opt.value = type;
            opt.textContent = type;
            clientFilterType.appendChild(opt);
        });

        clientFilterCities.innerHTML = '<option value="">Todos</option>';
        cities.forEach(type => {
            const opt = document.createElement('option');
            opt.value = type;
            opt.textContent = type;
            clientFilterCities.appendChild(opt);
        });
    }

    function clientFilterCompanies() {
        let locValue = clientFilterLocation.value;
        let typeValue = clientFilterType.value;
        let cityValue = clientFilterCities.value;
        let onlyActive = clientFilterActive.checked;
        let searchTerm = clientCompanySearchInput.value.trim().toLowerCase();

        clientFilteredCompanies = allCompanies.filter(c => {
            if(locValue && c.location !== locValue) return false;
            if(typeValue && c.type !== typeValue) return false;
            if(cityValue && c.city !== cityValue) return false;
            if(onlyActive && !c.active) return false;
            if(searchTerm && !c.name.toLowerCase().includes(searchTerm)) return false;
            return true;
        });
    }

    function renderClientCompanies() {
        clientCompaniesContainer.innerHTML = '';
        if(clientFilteredCompanies.length === 0) {
            clientCompaniesContainer.innerHTML = '<p>No se encontraron empresas.</p>';
            return;
        }
        clientFilteredCompanies.forEach(company => {
            const div = document.createElement('div');
            div.className = 'company-item';
            div.tabIndex = 0;
            div.setAttribute('role', 'listitem');
            div.style.cursor = 'default';
            const iconStatus = company.active ? '<i class="fa fa-check></i>' : '<i class="fa fa-lock></i>';
            div.innerHTML = `<strong>${company.name}</strong> <br><small>${company.address}</small><br><small><strong>${company.open !== undefined ? company.open : ''}</strong></small> ${iconStatus}`;
            clientCompaniesContainer.appendChild(div);
        });
    }

    clientFilterLocation.addEventListener('change', () => {
        clientFilterCompanies();
        renderClientCompanies();
    });
    clientFilterType.addEventListener('change', () => {
        clientFilterCompanies();
        renderClientCompanies();
    });
    clientFilterCities.addEventListener('change', () => {
        clientFilterCompanies();
        renderClientCompanies();
    });
    clientFilterActive.addEventListener('change', () => {
        clientFilterCompanies();
        renderClientCompanies();
    });
    clientCompanySearchInput.addEventListener('input', () => {
        clientFilterCompanies();
        renderClientCompanies();
    });
    clientBtnClearFilters.addEventListener('click', () => {
        clientFilterLocation.value = '';
        clientFilterType.value = '';
        clientFilterCities.value = '';
        clientFilterActive.checked = true;
        clientCompanySearchInput.value = '';
        clientFilterCompanies();
        renderClientCompanies();
    });

    // On page load client companies setup
    populateClientFilterSelects();
    clientFilterCompanies();
    renderClientCompanies();


    // Existing initialization for client and backoffice sections below ...
    // (Include your existing JS code for client booking steps, backoffice, etc.)


    // For demonstration: Initially hide client stepwizard when on companies tab
    function setActiveSection(id) {
        const sections = document.querySelectorAll('.section');
        sections.forEach(s => s.classList.remove('active'));
        const el = document.getElementById(id);
        if(el) el.classList.add('active');

        var wizard = document.getElementById('client-stepwizard');
        if(['client-listado', 'client-calendario', 'client-pagos', 'client-confirmacion'].includes(id)) {
            if(wizard) wizard.style.display = 'flex';
        } else {
            if(wizard) wizard.style.display = 'none';
        }
    }
    // Assign event listeners for nav links to switch sections and handle wizard visibility
    document.querySelectorAll('.nav-link-client').forEach(link => {
        link.addEventListener('click', e => {
            e.preventDefault();
            setActiveSection(e.target.dataset.target);
        });
    });
    document.querySelectorAll('.nav-link-backoffice').forEach(link => {
        link.addEventListener('click', e => {
            e.preventDefault();
            setActiveSection(e.target.dataset.target);
        });
    });

    // Set initial visible section
    setActiveSection('client-listado');

})();