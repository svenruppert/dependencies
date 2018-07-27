/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.dependencies.core.logger;

import java.util.logging.Level;



/**
 * <p>Abstract AbstractLogger class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class AbstractLogger implements LoggingService {

    /** {@inheritDoc} */
    @Override
    public void finest(String message) {
        log(Level.FINEST, message);
    }

    /** {@inheritDoc} */
    @Override
    public void finest(String message, Throwable thrown) {
        log(Level.FINEST, message, thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void finest(Throwable thrown) {
        log(Level.FINEST, thrown.getMessage(), thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void fine(String message) {
        log(Level.FINE, message);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFinestEnabled() {
        return isLoggable(Level.FINEST);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFineEnabled() {
        return isLoggable(Level.FINE);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String message) {
        log(Level.INFO, message);
    }

    /** {@inheritDoc} */
    @Override
    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    /** {@inheritDoc} */
    @Override
    public void severe(Throwable thrown) {
        log(Level.SEVERE, thrown.getMessage(), thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void severe(String message, Throwable thrown) {
        log(Level.SEVERE, message, thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(String message) {
        log(Level.WARNING, message);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(Throwable thrown) {
        log(Level.WARNING, thrown.getMessage(), thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(String message, Throwable thrown) {
        log(Level.WARNING, message, thrown);
    }
}
