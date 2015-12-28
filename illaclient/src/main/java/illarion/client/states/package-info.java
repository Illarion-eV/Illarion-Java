/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 * This package contains all the states the client can be in.
 *
 * These stages basically follow always the same structure.
 * <code>
 *        Loading
 *           ↓
 *     AccountSystem ←┐
 *     │     ↓        │
 *     │  Playing     │
 *     │     ↓        │
 *     │  Logout ─────┘
 *     │     ↓
 *     └──→ End
 * </code>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
package illarion.client.states;