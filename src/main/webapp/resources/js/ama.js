/**
 * ama.js
 * Interacciones JS mínimas compartidas por el layout base del portal AMA.
 * Nada aquí depende de PrimeFaces; es JS plano para no interferir con el ciclo
 * de vida AJAX de JSF.
 */

/**
 * Alterna la visibilidad del sidebar en pantallas móviles/tablet
 * (ver reglas @media en estilo.css: .ama-sidebar--open).
 */
function amaToggleSidebar() {
    var sidebar = document.getElementById('amaSidebar');
    if (sidebar) {
        sidebar.classList.toggle('ama-sidebar--open');
    }
}

/**
 * Alterna el atributo "type" de un campo de contraseña entre "password" y
 * "text", y cambia el ícono del ojo. Es JS puro (sin Ajax), así que no
 * dispara ningún ciclo de vida de JSF ni recarga la página.
 *
 * Se llama pasando "this" (el ícono/botón que el usuario clickeó). El botón
 * y el input deben compartir un contenedor con la clase "ama-password-wrap";
 * desde ahí se ubica el <input> con querySelector, evitando por completo
 * tener que conocer el id completo que JSF genera (ej. "loginForm:password",
 * que es distinto al "password" que se escribe en el XHTML).
 *
 * IMPORTANTE: si este botón vive dentro de un <h:form>, su elemento HTML
 * debe llevar type="button" explícito. Sin eso, un <button> por defecto es
 * type="submit" y el clic termina enviando todo el formulario en vez de
 * solo alternar el input.
 */
function amaTogglePassword(icon) {
    // Busca el input de contraseña dentro del contenedor padre
    let input = icon.parentElement.querySelector("input");

    // Verificamos si actualmente está oculta
    let oculto = input.type === 'password';

    // Alternamos el tipo de input
    input.type = oculto ? 'text' : 'password';

    // Alternamos el ícono de Google Material Symbols
    icon.textContent = oculto ? 'visibility_off' : 'visibility';

    // Actualizamos accesibilidad
    icon.setAttribute('aria-label', oculto ? 'Ocultar contraseña' : 'Mostrar contraseña');
}