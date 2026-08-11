import { useSyncExternalStore } from 'react';

/**
 * Matches Tailwind's `md` breakpoint, so a component that branches in JS stays in
 * step with the `md:` classes used elsewhere in the same file.
 */
const MOBILE_QUERY = '(max-width: 767px)';

const query = window.matchMedia(MOBILE_QUERY);

const subscribe = (onChange: () => void) => {
    query.addEventListener('change', onChange);
    return () => query.removeEventListener('change', onChange);
};

/**
 * True on phone-width screens.
 *
 * Reach for a `md:` Tailwind class first - this is only for things CSS cannot do,
 * such as feeding a different prop to an antd or MUI component (form layout,
 * scheduler height, number of visible days).
 *
 * useSyncExternalStore rather than useState + useEffect: it reads the real value on
 * the very first render, so a mobile visitor never sees one frame of desktop layout.
 */
export function useIsMobile(): boolean {
    return useSyncExternalStore(
        subscribe,
        () => query.matches,
        () => false, // no window while server-rendering; assume desktop
    );
}
