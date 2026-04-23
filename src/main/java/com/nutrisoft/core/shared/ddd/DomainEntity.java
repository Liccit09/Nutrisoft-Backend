package com.nutrisoft.core.shared.ddd;

import static lombok.AccessLevel.PROTECTED;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Base class for all Domain Entities with Identity. Implements Value Object pattern for Identity
 * comparison.
 *
 * <p>Located in: Shared Kernel - Core Layer This class is shared by all components within the Core
 *
 * @param <I> The type of identifier
 */
@RequiredArgsConstructor(access = PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class DomainEntity<I extends Identifier<?>> {

  protected final I id;
}
