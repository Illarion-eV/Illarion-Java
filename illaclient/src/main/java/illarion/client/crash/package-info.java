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
 * The crash packages contains the different crash handlers that kick in, in
 * case a part of the client crashes. According to the part of the client the
 * handlers either try to restart the part of the client in question or stop the
 * client completely.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
package illarion.client.crash;